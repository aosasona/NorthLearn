package com.trulyao.northlearn.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.VisualTransformation.Companion.None
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundedInput(
    value: String,
    onChange: (String) -> Unit,
    placeholder: @Composable () -> Unit,
    keyboardOptions: KeyboardOptions,
    visualTransformation: VisualTransformation? = null,
) {
    TextField(
        value = value,
        onValueChange = { onChange(it) },
        placeholder = placeholder,
        modifier = Modifier
            .fillMaxWidth(),
        maxLines = 1,
        textStyle = TextStyle(color = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(8.dp),
        keyboardOptions = keyboardOptions,
        visualTransformation = if (visualTransformation !== null) visualTransformation else None,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        )
    )
}