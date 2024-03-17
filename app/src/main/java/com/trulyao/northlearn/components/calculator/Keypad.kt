package com.trulyao.northlearn.components.calculator

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class KeyType {
    Number, // Just number 0-9
    Operand, // Operands like +, -, x etc
    Special // Neither numbers nor operands like "CE" (Clear Everything), Delete etc
}

enum class Key(val value: String, val type: KeyType) {
    // Number
    One("1", KeyType.Number),
    Two("2", KeyType.Number),
    Three("3", KeyType.Number),
    Four("4", KeyType.Number),
    Five("5", KeyType.Number),
    Six("6", KeyType.Number),
    Seven("7", KeyType.Number),
    Eight("8", KeyType.Number),
    Nine("9", KeyType.Number),
    Zero("0", KeyType.Number),
    Dot(
        ".",
        KeyType.Number
    ), // treating it as a number because it needs to be part of the "current buffer" (needs to be rendered)

    // Operand
    Plus("+", KeyType.Operand),
    Subtract("-", KeyType.Operand),
    Divide("/", KeyType.Operand),
    Multiply("x", KeyType.Operand),
    Exponent("^", KeyType.Operand),
    Equals("=", KeyType.Operand),

    // Special keys
    Clear("CE", KeyType.Special),
    Delete("⌫", KeyType.Special),

    None("", KeyType.Operand); // the default "empty" state for current operand
}

// Buttons "matrix" to render as a grid
val buttons = listOf(
    listOf(Key.Clear, Key.Delete, Key.Exponent, Key.Plus),
    listOf(Key.Nine, Key.Eight, Key.Seven, Key.Subtract),
    listOf(Key.Six, Key.Five, Key.Four, Key.Multiply),
    listOf(Key.Three, Key.Two, Key.One, Key.Divide),
    listOf(Key.Zero, Key.Dot, Key.Equals),
);


@Composable
fun Keypad(handleKeyPress: (Key) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        buttons.forEach { members ->
            Row(modifier = Modifier.fillMaxWidth()) {
                members.forEach { button ->
                    Button(
                        onClick = { handleKeyPress(button) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (button.type != KeyType.Number) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.secondary
                            },

                            contentColor = if (button.type != KeyType.Number) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSecondary
                            }
                        ),
                        modifier = Modifier
                            .height(95.dp)
                            .clip(CircleShape)
                            .padding(7.dp)
                            .weight(if (button == Key.Equals) 0.5f else 0.25f)
                    ) {
                        Text(
                            button.value,
                            fontSize = if (button.type != KeyType.Number) 24.sp else 22.sp
                        )
                    }
                }
            }
        }
    }

}