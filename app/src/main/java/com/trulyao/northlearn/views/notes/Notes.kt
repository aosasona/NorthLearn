package com.trulyao.northlearn.views.notes

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.trulyao.northlearn.models.Content
import com.trulyao.northlearn.models.NoteService
import com.trulyao.northlearn.models.NoteViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Notes(navController: NavController, noteViewModel: NoteViewModel = viewModel()) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val noteService = NoteService(context)

    var isLoading by remember { mutableStateOf(true) }
    val contents = remember { mutableStateListOf<Content>() }

    fun loadContent() {
        isLoading = true
        contents.clear()
        contents.addAll(noteService.listAllFiles(noteViewModel.uiState.currentDirectory.value))
        isLoading = false
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

    Scaffold() { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(contentPadding)
                .padding(20.dp)
        ) {
            stickyHeader { Header(loadContent = { loadContent() }, noteViewModel = noteViewModel) }

            items(contents) { content ->
                Surface(
                    onClick = { /* TODO: navigate */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = content.name,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .size(50.dp)
                            .wrapContentHeight()
                    )
                }

                Divider(color = Color.DarkGray, modifier = Modifier.padding(start = 10.dp))
            }
        }
    }
}

@Composable
fun Header(loadContent: () -> Unit, noteViewModel: NoteViewModel) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Notes", fontSize = 36.sp, fontWeight = FontWeight(700))

        Row {
            IconButton(onClick = { loadContent() }) {
                Icon(Icons.Default.Refresh, "Reload")
            }

            Spacer(modifier = Modifier.size(8.dp))

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Filled.MoreVert, "Menu")
                }

                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
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
                    }, onClick = { })

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
                        }, onClick = { /*TODO*/ })
                }
            }
        }
    }
}

@Composable
fun ActionButton() {
    ExtendedFloatingActionButton(onClick = { /*TODO*/ }) {
        // TODO: complete this
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NotesPreview() {
    val navController = rememberNavController()
    Notes(navController = navController)
}