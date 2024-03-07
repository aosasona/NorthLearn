package com.trulyao.northlearn.views

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.trulyao.northlearn.ui.theme.NorthLearnTheme
import com.trulyao.northlearn.utils.Store
import com.trulyao.northlearn.utils.StoreKey
import com.trulyao.northlearn.views.quiz.Quiz


@Composable
fun Root(applicationContext: Context) {
    val context = LocalContext.current
    val navController = rememberNavController()

    val isSignedIn by Store.get(context, key = StoreKey.IsSignedIn, default = false)
        .collectAsState(initial = false)


    NorthLearnTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(
                navController = navController,
                startDestination = if (isSignedIn) {
                    Views.Home.name
                } else {
                    Views.Login.name
                }
            ) {
                composable(Views.Login.name) {
                    Login()
                }

                composable(Views.Home.name) {
                    Home()
                }

                composable(Views.Quiz.name) {
                    Quiz()
                }
            }
        }
    }
}