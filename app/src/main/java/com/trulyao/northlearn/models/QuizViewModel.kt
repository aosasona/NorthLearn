package com.trulyao.northlearn.models

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.nio.charset.Charset
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.readText

// Interface (acting like an algebraic data type) for representing the various states the UI can be in
sealed interface QuizViewState {
    data class Success(val animals: List<Animal>) : QuizViewState
    object Error : QuizViewState
    object Loading : QuizViewState
}

class QuizViewModel(context: Context) : ViewModel() {
    var uiState: QuizViewState by mutableStateOf(QuizViewState.Loading)
        private set

    val SOURCE_PATH = Path(context.filesDir.toString(), "animals.json")

    // Fetch animals from remote API if the local cache (aka animals.json on disk) doesn't contain it
    private fun getAnimals() {
        viewModelScope.launch {
            uiState = try {
                var rawData: String = if (dataFileExists()) readDataFile() else loadDataFromAPI();

                QuizViewState.Loading // temp
            } catch (e: Exception) {
                QuizViewState.Error
            }
        }
    }

    private fun dataFileExists(): Boolean {
        return SOURCE_PATH.exists()
    }

    private fun readDataFile(): String {
        return SOURCE_PATH.readText(Charset.defaultCharset())
    }

    private suspend fun loadDataFromAPI(): String {
        return AnimalsAPI.retrofitService.getAnimals()
    }
}