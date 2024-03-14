package com.trulyao.northlearn.views

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.trulyao.northlearn.models.QuizViewModel
import com.trulyao.northlearn.ui.theme.NorthLearnTheme
import com.trulyao.northlearn.utils.Store
import com.trulyao.northlearn.utils.StoreKey
import com.trulyao.northlearn.views.notes.Notes
import com.trulyao.northlearn.views.quiz.Quiz
import com.trulyao.northlearn.views.quiz.QuizResult
import com.trulyao.northlearn.views.quiz.QuizSplash


@Composable
fun Root(applicationContext: Context) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val quizViewModel: QuizViewModel = viewModel()

    val signedInUser by Store.get(context, key = StoreKey.User, default = null)
        .collectAsState(initial = 0)

    LaunchedEffect("root") {
        quizViewModel.load(context)
    }


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

                composable(Views.QuizSplash.name) { QuizSplash(navController = navController) }
                composable(Views.Quiz.name) { Quiz(navController, quizViewModel) }
                composable(Views.QuizScore.name) { QuizResult(navController, quizViewModel) }

                composable(
                    "${Views.Notes.name}/{folderName}",
                    arguments = listOf(navArgument("folderName") {
                        type = NavType.StringType
                    })
                ) { backStackEntry ->
                    Notes(
                        navController = navController,
                        currentFolderProp = backStackEntry.arguments?.getString("folderName")
                    )
                }
            }
        }
    }
}
