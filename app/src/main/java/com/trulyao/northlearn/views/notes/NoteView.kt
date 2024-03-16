package com.trulyao.northlearn.views.notes

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.trulyao.northlearn.components.Loading
import com.trulyao.northlearn.models.AppException
import com.trulyao.northlearn.models.NoteModel
import com.trulyao.northlearn.models.NoteService
import kotlinx.coroutines.launch
import kotlin.io.path.Path

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteView(navController: NavController, currentNote: String?) {

    val context = LocalContext.current
    val noteService = NoteService(context)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    if (currentNote.isNullOrEmpty()) {
        navController.navigateUp()
        return
    }


    val notePath = currentNote.split(".txt")[0].replace(
        ".",
        "/"
    ) + ".txt" // replace all dots with / except the extension

    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var note by remember {
        mutableStateOf<NoteModel>(
            NoteModel(
                name = "",
                path = Path(notePath),
                content = mutableStateOf("")
            )
        )
    }
    val enableSaveButton by remember {
        derivedStateOf { note.content.value.trim().isNotEmpty() }
    }

    fun handleException(e: Exception) {
        System.err.println(e)
        val message = when (e) {
            is AppException -> e.message
            else -> "Something went wrong"
        }
        scope.launch {
            snackbarHostState.showSnackbar(message!!)
        }
    }

    fun save() {
        if (!enableSaveButton) return;

        scope.launch {
            try {
                isSaving = true
                noteService.saveNote(note.path, note.content.value)
                Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                handleException(e)
            } finally {
                isSaving = false
            }
        }
    }

    fun load() {
        scope.launch {
            try {
                isLoading = true
                note = noteService.getNote(notePath)
            } catch (e: Exception) {
                handleException(e)
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect("note") {
        load()
    }

    if (isLoading) {
        return Loading()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        note.name,
                        modifier = Modifier.wrapContentSize(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.outline,
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { save() }, enabled = enableSaveButton && !isSaving) {
                        if (isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(8.dp))
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "Save content"
                            )
                        }
                    }
                },
            )
        }
    ) { contentPadding ->
        TextField(
            value = note.content.value,
            onValueChange = { value -> note.content.value = value },
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
            ),
            modifier = Modifier
                .background(MaterialTheme.colorScheme.outline)
                .padding(contentPadding)
                .fillMaxSize()
                .border(
                    width = 0.dp,
                    color = MaterialTheme.colorScheme.background,
                    shape = RectangleShape
                )
        )
    }
}