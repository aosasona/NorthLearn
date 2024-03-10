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
import com.trulyao.northlearn.views.notes.Notes
import com.trulyao.northlearn.views.quiz.Quiz
import com.trulyao.northlearn.views.quiz.QuizSplash


@Composable
fun Root(applicationContext: Context) {
    val context = LocalContext.current
    val navController = rememberNavController()

    val signedInUser by Store.get(context, key = StoreKey.User, default = null)
        .collectAsState(initial = 0)


    NorthLearnTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(
                navController = navController,
                startDestination = if (signedInUser !== null) {
                    Views.Home.name
                } else {
                    Views.Login.name
                }
            ) {
                composable(Views.Login.name) { Login() }

                composable(Views.Home.name) {
                    Home(
                        navController = navController,
                        userID = signedInUser
                    )
                }

                composable(Views.Quiz.name) { Quiz() }
                composable(Views.QuizSplash.name) { QuizSplash(navController = navController) }

                composable(Views.Notes.name) { Notes() }
            }
        }
    }
}
