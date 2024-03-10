package com.trulyao.northlearn.models

import android.content.Context
import androidx.compose.runtime.getValue
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

data class CurrentRound(var selectedAnimals: List<Animal> = listOf(), var score: Int = 0);

// Interface (acting like an algebraic data type) for representing the various states the UI can be in at any given time
sealed interface QuizViewState {
    data class Success(val animals: List<Animal>, val currentRound: CurrentRound) : QuizViewState
    data object Error : QuizViewState
    data object Loading : QuizViewState
}

class QuizViewModel() : ViewModel() {
    var uiState: QuizViewState by mutableStateOf(QuizViewState.Loading)
        private set

    public fun load(context: Context) {
        getAnimals(context)
        startRound()
    }

    // Select random animals from the dataset
    public fun startRound() {
    }

    private fun getDataPath(context: Context): Path {
        return Path(context.filesDir.toString(), "animals.json")
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
        val rawData = getDataPath(context).readText(Charset.defaultCharset())
        return Json.decodeFromString<List<Animal>>(rawData);
    }

    // Fetch the data set from the remote API and cache it locally for faster retrieval next time
    private suspend fun loadDataFromAPI(context: Context): List<Animal> {
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