package com.trulyao.northlearn.components.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun Header(
    loadContent: () -> Unit,
    showHeaderMenu: MutableState<Boolean>,
    showCreateFolderDialog: MutableState<Boolean>,
    showCreateNoteDialog: MutableState<Boolean>,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
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
                        DropdownMenuItem(
                            text = {
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
                            },
                            onClick = { showCreateFolderDialog.value = true }
                        )

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
                            }, onClick = { showCreateNoteDialog.value = true }
                        )
                    }
                }
            }
        }
    }
}
