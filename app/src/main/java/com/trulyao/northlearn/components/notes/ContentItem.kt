package com.trulyao.northlearn.components.notes

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Note
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trulyao.northlearn.models.Content
import com.trulyao.northlearn.models.NoteService
import com.trulyao.northlearn.models.bytesToHumanReadableFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.nio.file.Path

@Composable
fun ContentItem(
    noteService: NoteService,
    scope: CoroutineScope,
    content: Content,
    reload: () -> Unit,
) {
    var showDropDownMenu by remember { mutableStateOf(false) }

    fun deleteItem(path: Path) {
        scope.launch {
            showDropDownMenu = false
            noteService.deleteItem(path)
            reload()
        }
    }

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
                        onClick = { deleteItem(content.absolutePath) }
                    )
                }
            }

        }

    }
}
