package com.trulyao.northlearn.views.notes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Note
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.trulyao.northlearn.components.PromptDialog
import com.trulyao.northlearn.models.AppException
import com.trulyao.northlearn.models.Content
import com.trulyao.northlearn.models.NoteService
import com.trulyao.northlearn.models.bytesToHumanReadableFormat
import com.trulyao.northlearn.views.Views
import kotlinx.coroutines.launch
import kotlin.io.path.Path

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Notes(navController: NavController, currentFolderProp: String?) {
    val currentFolder = if (currentFolderProp == "(null)") null else currentFolderProp

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

    fun loadContent() {
        scope.launch {
            isLoading = true
            val results = noteService.listAllFiles(currentFolder)
            contents.clear()
            contents.addAll(results)
            isLoading = false
        }
    }

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

    fun createFolder(folderName: String) {
        scope.launch {
            try {
                val name = sanitize(folderName) // clean up all other "illegal" characters
                if (name.isEmpty()) throw AppException("Folder name must be at least one character")
                noteService.createDirectory(
                    Path(folderName, folderName)
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
                val note = noteService.createFile(
                    Path(currentFolder ?: "", "${filename}.txt")
                )
                loadContent() // force view to reload
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    fun handleContentItemClick(content: Content) {
        if (content.isDirectory) {
            if (content.name.isEmpty()) return;
            // change the current directory and "re-navigate" to retain history
            navController.navigate("${Views.Notes.name}/${currentFolder}")
        }
    }

    LaunchedEffect("notes") {
        loadContent()
    }

    if (isLoading) {
        return Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(40.dp),
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.size(18.dp))

            Text(text = "Loading...", color = MaterialTheme.colorScheme.secondary)
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
                    onClick = { /* TODO: navigate */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ContentItem(
                        content = content,
                        noteService = noteService,
                        reload = { loadContent() })
                }
            }
        }

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
}

@Composable
fun Header(
    loadContent: () -> Unit,
    showHeaderMenu: MutableState<Boolean>,
    showCreateFolderDialog: MutableState<Boolean>,
    showCreateNoteDialog: MutableState<Boolean>,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(text = "Notes", fontSize = 36.sp, fontWeight = FontWeight(700))

        Row {
            IconButton(onClick = { loadContent() }) {
                Icon(Icons.Default.Refresh, "Reload")
            }

            Spacer(modifier = Modifier.size(8.dp))

            Box {
                IconButton(onClick = { showHeaderMenu.value = true }) {
                    Icon(Icons.Filled.MoreVert, "Menu")
                }

                DropdownMenu(
                    expanded = showHeaderMenu.value,
                    onDismissRequest = { showHeaderMenu.value = false }) {
                    DropdownMenuItem(text = {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        ) {
                            Icon(
                                Icons.Default.CreateNewFolder,
                                contentDescription = "Create new folder",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.size(10.dp))
                            Text("Create folder")
                        }
                    }, onClick = { showCreateFolderDialog.value = true })

                    DropdownMenuItem(
                        text = {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            ) {
                                Icon(
                                    Icons.Default.Create,
                                    contentDescription = "New note",
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.size(10.dp))
                                Text("New note")
                            }
                        }, onClick = { showCreateNoteDialog.value = true })
                }
            }
        }
    }
}

@Composable
fun ContentItem(noteService: NoteService, content: Content, reload: () -> Unit) {
    var showDropDownMenu by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
        if (content.isDirectory) {
            Box(
                modifier = Modifier
                    .size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Folder,
                    "Folder",
                    modifier = Modifier.size(24.dp),
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(4.dp)
                    ), contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Outlined.Note, "Folder", modifier = Modifier.size(20.dp))
            }
        }

        Spacer(modifier = Modifier.size(14.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(0.75f)
            ) {
                Text(
                    text = content.name,
                    modifier = Modifier.wrapContentSize(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.size(5.dp))

                Text(
                    bytesToHumanReadableFormat(content.size),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.size(5.dp))

            IconButton(onClick = { showDropDownMenu = true }) {
                Icon(Icons.Default.MoreVert, "Options")

                DropdownMenu(
                    expanded = showDropDownMenu,
                    onDismissRequest = { showDropDownMenu = false }) {
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    "Delete",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.size(6.dp))
                                Text(text = "Delete")
                            }
                        },
                        onClick = { /*TODO*/ }
                    )
                }
            }

        }

    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NotesPreview() {
    val navController = rememberNavController()
    Notes(navController = navController, "")
}