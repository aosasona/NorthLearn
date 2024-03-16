package com.trulyao.northlearn.views.calculator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trulyao.northlearn.components.calculator.Key
import com.trulyao.northlearn.components.calculator.KeyType
import com.trulyao.northlearn.components.calculator.Keypad
import kotlin.math.pow


@Composable
fun Calculator() {
    var currentValue by remember { mutableStateOf("") }
    var previousValue by remember { mutableStateOf("") }
    var operand by remember { mutableStateOf(Key.None) }

    fun formatForDisplay(value: String): String {
        if (value.endsWith(".")) return value; // handle cases where the user is just entering a decimal point

        return (if (value.toFloat() % 1.0 == 0.0) {
            "%,d".format(value.toFloat().toInt())
        } else {
            "%,9f".format(value.toFloat()).trimEnd('0').trimEnd('.')
        }).trim().trimEnd(',')
    }

    fun handleKeyPress(key: Key) {
        when (key.type) {
            KeyType.Number -> {
                if (currentValue.length >= 9) return;
                if (key.value == "." && currentValue.contains(".")) return;
                currentValue += key.value
            }

            KeyType.Special -> when (key) {
                Key.Delete -> {
                    if (currentValue.isEmpty()) return;
                    currentValue = currentValue.slice(0..currentValue.length - 2)
                }

                Key.Clear -> {
                    currentValue = "";
                    previousValue = "";
                    operand = Key.None;
                }

                else -> print("Unhandled special key")
            }

            KeyType.Operand -> {
                when (key) {
                    Key.None -> return;
                    Key.Equals -> {
                        val prev = previousValue.toFloatOrNull()
                        val curr = currentValue.toFloatOrNull()
                        if (prev == null || curr == null || operand == Key.None) return;

                        val result = when (operand) {
                            Key.Plus -> prev + curr
                            Key.Subtract -> prev - curr
                            Key.Multiply -> prev * curr
                            Key.Divide -> prev / curr
                            Key.Exponent -> prev.pow(curr.toInt())
                            else -> {
                                println("Unknown operand detected")
                                curr
                            }
                        }

                        operand = Key.None
                        previousValue = ""
                        currentValue =
                            if ((result % 1.0) == 0.0) result.toInt().toString() else "%9f".format(
                                result
                            )
                    }

                    else -> {
                        // When you use any of the operands, the next input should be into the currentValue, there are some nuances though
                        // If the previous value has something in it, we leave it there
                        // If the previous value has nothing  in it, we move the current value to the previous value so that subsequent input can go in there
                        operand = key
                        if (previousValue.isEmpty()) {
                            previousValue = currentValue
                            currentValue = ""
                        }
                    }
                }
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 10.dp)
                .padding(horizontal = 12.dp)
                .fillMaxHeight()
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        // This is a very hacky way but whatever
                        if (currentValue.isNotEmpty()) {
                            formatForDisplay(currentValue)
                        } else if (previousValue.isNotEmpty()) {
                            formatForDisplay(previousValue)
                        } else {
                            ""
                        },
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 80.sp,
                        lineHeight = 90.sp
                    )
                }
            }

            Keypad(handleKeyPress = { key -> handleKeyPress(key) })
        }
    }
}