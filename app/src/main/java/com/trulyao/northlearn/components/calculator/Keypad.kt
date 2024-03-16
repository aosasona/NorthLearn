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
    Number,
    Operand,
    Special
}

enum class Key(val value: String, val type: KeyType) {
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
    Dot(".", KeyType.Number),
    Plus("+", KeyType.Operand),
    Subtract("-", KeyType.Operand),
    Divide("/", KeyType.Operand),
    Multiply("x", KeyType.Operand),
    Exponent("^", KeyType.Operand),
    Equals("=", KeyType.Operand),
    Clear("CE", KeyType.Special),
    Delete("⌫", KeyType.Special),
    None("", KeyType.Operand);
}

val buttons = listOf(
    listOf(Key.Clear, Key.Delete, Key.Exponent, Key.Plus),
    listOf(Key.Nine, Key.Eight, Key.Seven, Key.Subtract),
    listOf(Key.Six, Key.Five, Key.Four, Key.Multiply),
    listOf(Key.Three, Key.Two, Key.One, Key.Divide),
    listOf(Key.Zero, Key.Dot, Key.Equals),
);


@Composable
fun Keypad(handleKeyPress: (Key) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
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
                            fontSize = if (button.type != KeyType.Number) 22.sp else 20.sp
                        )
                    }
                }
            }
        }
    }

}