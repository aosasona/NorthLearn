package com.trulyao.northlearn.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromptDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    onConfirmation: (String) -> Unit,
    dialogTitle: String,
    dialogText: String,
) {
    var value by remember { mutableStateOf("") }

    when {
        visible -> AlertDialog(
            title = { Text(text = dialogTitle) },
            text = {
                RoundedInput(
                    value = value,
                    onChange = { value = it },
                    placeholder = { Text(dialogText) },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        autoCorrect = false,
                        capitalization = KeyboardCapitalization.None
                    )
                )
            },
            onDismissRequest = { onDismiss() },
            confirmButton = {
                TextButton(onClick = {
                    onConfirmation(value)
                    value = ""
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text("Dismiss")
                }
            }
        )
    }
}