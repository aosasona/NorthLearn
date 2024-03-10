package com.trulyao.northlearn.views.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.trulyao.northlearn.models.QuizViewModel
import com.trulyao.northlearn.models.QuizViewState

@Composable
fun Quiz(quizViewModel: QuizViewModel = viewModel()) {

    when (quizViewModel.uiState) {
        is QuizViewState.Loading -> LoadingScreen()
        is QuizViewState.Error -> ErrorScreen()
        else -> Box {}
    }
}

@Composable
fun LoadingScreen() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(
            modifier = Modifier.width(40.dp),
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.size(18.dp))

        Text(text = "Wait a second...", color = MaterialTheme.colorScheme.secondary)
    }
}

@Composable
fun ErrorScreen() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color.Red)
            .fillMaxSize()
    ) {
        Text(text = "Something went wrong, please restart the app and try again.")
    }
}