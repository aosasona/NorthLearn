package com.trulyao.northlearn.views

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.trulyao.northlearn.utils.Store
import com.trulyao.northlearn.utils.StoreKey
import kotlinx.coroutines.launch

@Composable
fun Home() {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    fun logout() {
        coroutineScope.launch {
            Store.set(context, key = StoreKey.IsSignedIn, value = false)
        }
    }

    Column {
        TextButton(onClick = { logout() }) {
            Text("Sign Out")
        }
    }
}