package com.trulyao.northlearn.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

typealias OnClick = () -> Unit;
typealias Content = @Composable () -> Unit

@Composable
fun RoundedButton(onClick: OnClick, enabled: Boolean = true, content: Content) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
            .fillMaxWidth(),
        enabled = enabled
    ) {
        Box(modifier = Modifier.padding(vertical = 8.dp)) {
            content()
        }
    }
}