package com.trulyao.northlearn.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class NoteViewState(
    var currentDirectory: MutableState<String?> = mutableStateOf(null),
    var currentFile: MutableState<String?> = mutableStateOf(null),
)

class NoteViewModel : ViewModel() {
    var uiState by mutableStateOf(NoteViewState())
        private set

    public fun setCurrentFolder(folderName: String) {
        uiState.currentDirectory.value = folderName
    }

    public fun setCurrentFile(fileName: String) {
        uiState.currentFile.value = fileName
    }
}