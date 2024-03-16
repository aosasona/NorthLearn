package com.trulyao.northlearn.views.notes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.trulyao.northlearn.components.Loading
import com.trulyao.northlearn.components.PromptDialog
import com.trulyao.northlearn.components.notes.ContentItem
import com.trulyao.northlearn.components.notes.Header
import com.trulyao.northlearn.models.AppException
import com.trulyao.northlearn.models.Content
import com.trulyao.northlearn.models.NoteService
import com.trulyao.northlearn.views.Views
import kotlinx.coroutines.launch
import kotlin.io.path.Path

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Notes(navController: NavController, currentFolderProp: String?) {
    val currentFolder = (if (currentFolderProp == "(null)")
        null
    else currentFolderProp)?.replace(".", "/")

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val noteService = NoteService(context)

    var isLoading by remember { mutableStateOf(true) }
    val contents = remember { mutableStateListOf<Content>() }

    // Visibility states
    val showHeaderMenu = remember { mutableStateOf(false) }
    val showCreateFolderDialog = remember { mutableStateOf(false) }
    val showCreateNoteDialog = remember { mutableStateOf(false) }

    fun sanitize(text: String): String {
        return text.replace(Regex("[^a-zA-Z0-9_\\s-]+"), "")
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

    fun loadContent() {
        scope.launch {
            try {
                isLoading = true
                val results = noteService.listAllFiles(currentFolder)
                contents.clear()
                contents.addAll(results)
            } catch (e: Exception) {
                handleException(e)
            } finally {
                isLoading = false
            }
        }
    }


    fun createFolder(folderName: String) {
        scope.launch {
            try {
                val name = sanitize(folderName) // clean up all other "illegal" characters
                if (name.isEmpty()) throw AppException("Folder name must be at least one character")
                noteService.createDirectory(
                    Path(currentFolder ?: "", name)
                )
                loadContent() // force view to reload
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    fun createFile(filename: String) {
        scope.launch {
            try {
                val name = sanitize(filename) // clean up all other "illegal" characters
                if (name.isEmpty()) throw AppException("File name must be at least one character")
                noteService.createFile(Path(currentFolder ?: "", "${name}.txt"))
                loadContent() // force view to reload
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    fun handleContentItemClick(content: Content) {
        if (content.name.isEmpty()) return;

        // We need to convert the path to the format "x.x.x" to make it usable as a path param, mainly for nested folders, e.g. "${Views.Notes.name}/foo/bar/baz" would not work as a valid nav route
        val target = noteService.pathToNavFriendlyString(content.path)

        if (content.isDirectory) {
            navController.navigate("${Views.Notes.name}/${target}")
        } else {
            navController.navigate("${Views.Note.name}/${target}")
        }
    }

    LaunchedEffect("notes") {
        loadContent()
    }

    @Composable
    fun CreationDialogs() {
        // Create folder prompt
        PromptDialog(
            visible = showCreateFolderDialog.value,
            onDismiss = { showCreateFolderDialog.value = false },
            onConfirmation = { name ->
                createFolder(name)
                showCreateFolderDialog.value = false
                showHeaderMenu.value = false
            },
            dialogTitle = "Create folder",
            dialogText = "Enter folder name here"
        )

        // Create note prompt
        PromptDialog(
            visible = showCreateNoteDialog.value,
            onDismiss = { showCreateNoteDialog.value = false },
            onConfirmation = { name ->
                createFile(name)
                showCreateNoteDialog.value = false
                showHeaderMenu.value = false
            },
            dialogTitle = "New note",
            dialogText = "Enter a filename"
        )
    }

    if (isLoading) {
        return Loading()
    } else if (contents.isEmpty()) {
        return Scaffold(topBar = {
            Header(
                loadContent = { loadContent() },
                showHeaderMenu = showHeaderMenu,
                showCreateFolderDialog = showCreateFolderDialog,
                showCreateNoteDialog = showCreateNoteDialog
            )
        }) { contentPadding ->
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
            ) {
                Icon(
                    Icons.Default.FolderOpen,
                    "Empty",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.outline
                )

                Spacer(modifier = Modifier.size(10.dp))

                Text(text = "Nothing here yet...", color = MaterialTheme.colorScheme.outline)
            }

            CreationDialogs()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            stickyHeader {
                Header(
                    loadContent = { loadContent() },
                    showHeaderMenu = showHeaderMenu,
                    showCreateFolderDialog = showCreateFolderDialog,
                    showCreateNoteDialog = showCreateNoteDialog
                )
            }

            items(contents) { content ->
                Surface(
                    onClick = { handleContentItemClick(content) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ContentItem(
                        content = content,
                        scope = scope,
                        noteService = noteService,
                        reload = { loadContent() }
                    )
                }
            }
        }

        CreationDialogs()
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NotesPreview() {
    val navController = rememberNavController()
    Notes(navController = navController, "")
}