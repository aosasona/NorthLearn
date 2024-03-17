package com.trulyao.northlearn.models

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.charset.Charset
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlin.random.Random

data class Question(
    val animal: Animal,
    var answer: MutableState<String> = mutableStateOf(""),
    var hasBeenMarked: MutableState<Boolean> = mutableStateOf(false),
)

data class CurrentRound(
    var questions: MutableList<Question> = mutableStateListOf(),
    var score: MutableState<Int> = mutableIntStateOf(0),
    var currentQuestion: MutableState<Int> = mutableIntStateOf(0),
);

// Interface (acting like an algebraic data type) for representing the various states the UI can be in at any given time
sealed interface QuizViewState {
    data class Success(val animals: List<Animal>, var currentRound: CurrentRound) : QuizViewState
    data object Error : QuizViewState
    data object Loading : QuizViewState
}

class QuizViewModel() : ViewModel() {
    public val numQuestions = 10
    var uiState: QuizViewState by mutableStateOf(QuizViewState.Loading)
        private set

    public fun load(context: Context) {
        getAnimals(context)
    }

    // Select random animals from the dataset
    public fun startRound() {
        if (uiState !is QuizViewState.Success) return;

        reset()

        val state = (uiState as QuizViewState.Success)
        val dataSetSize = state.animals.size

        var attempts = 0 // this is used to make sure we don't infinitely retry
        val selectedIndices = arrayListOf<Int>()

        // this runs as long as is required (and under a certain limit) to make sure we don't have duplicate questions
        while (selectedIndices.size < numQuestions && attempts <= 5) {
            val randomIndices = List(numQuestions) { Random.nextInt(0, dataSetSize) }.distinct()
            selectedIndices.addAll(randomIndices)
            attempts += 1
        }


        // Append the questions to the current round
        for (index in selectedIndices) {
            if (state.animals.size < index) continue
            val question = Question(animal = state.animals[index])
            state.currentRound.questions.add(question)
        }
    }

    // Util method to update the current round's points with some guardrails
    public fun addPoints() {
        if (uiState !is QuizViewState.Success) return;
        val state = (uiState as QuizViewState.Success)

        if (state.currentRound.score.equals(state.currentRound.questions.size)) return;
        state.currentRound.score.value += 10
    }

    // Reset the state of the current round
    private fun reset() {
        if (uiState !is QuizViewState.Success) return;
        val state = (uiState as QuizViewState.Success)

        state.currentRound.score.value = 0
        state.currentRound.currentQuestion.value = 0
        state.currentRound.questions.clear()
    }

    private fun getDataPath(context: Context): Path {
        return Path(context.filesDir.toString(), "dataset.json")
    }

    // Fetch animals from remote API if the local cache (aka animals.json on disk) doesn't contain it
    private fun getAnimals(context: Context) {
        viewModelScope.launch {
            uiState = QuizViewState.Loading
            uiState = try {
                val data: List<Animal> =
                    if (dataFileExists(context)) readDataFile(context) else loadDataFromAPI(context);

                QuizViewState.Success(animals = data, currentRound = CurrentRound())
            } catch (e: Exception) {
                System.err.print(e.message)
                QuizViewState.Error
            }
        }
    }

    private fun dataFileExists(context: Context): Boolean {
        return getDataPath(context).exists();
    }

    // Read and deserialize the dataset from the on-disk cache
    private fun readDataFile(context: Context): List<Animal> {
        println("========> Reading from local cache")
        val rawData = getDataPath(context).readText(Charset.defaultCharset())
        return Json.decodeFromString<List<Animal>>(rawData);
    }

    // Fetch the data set from the remote API and cache it locally for faster retrieval next time
    private suspend fun loadDataFromAPI(context: Context): List<Animal> {
        println("========> Fetching data from API")
        val dataSet = AnimalsAPI.retrofitService.getAnimals()
        saveDataSetToDisk(context, dataSet)
        return dataSet;
    }

    // Save the dataset to disk
    private fun saveDataSetToDisk(context: Context, dataSet: List<Animal>) {
        val dataStr = Json.encodeToString(dataSet)
        getDataPath(context).writeText(dataStr);
    }
}