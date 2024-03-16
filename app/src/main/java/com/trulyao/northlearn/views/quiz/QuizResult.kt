package com.trulyao.northlearn.views.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.trulyao.northlearn.models.QuizViewModel
import com.trulyao.northlearn.models.QuizViewState
import com.trulyao.northlearn.views.Views

@Composable
fun QuizResult(navController: NavController, quizViewModel: QuizViewModel) {
    val state = quizViewModel.uiState as QuizViewState.Success

    val percent by remember {
        derivedStateOf {
            (state.currentRound.score.value / (quizViewModel.numQuestions * 10)) * 100
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        if (percent >= 50.00) {
            Text(
                "Passed!", fontSize = 32.sp, fontWeight = FontWeight(700),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.size(12.dp))

            Text(
                "You did great, congratulations!",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            Text(
                "Oops",
                fontSize = 32.sp,
                fontWeight = FontWeight(700),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.size(12.dp))

            Text(
                "Chin up, you'll do better next time!",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        Spacer(modifier = Modifier.size(12.dp))

        Text(
            "${state.currentRound.score.value}/${state.currentRound.questions.size * 10}",
            fontSize = 64.sp,
            fontWeight = FontWeight(800),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.size(32.dp))

        // Buttons in bottom of the screen
        Row(horizontalArrangement = Arrangement.End) {
            TextButton(onClick = { navController.navigate(Views.Home.name) }) {
                Text("Go Home")
            }

            Spacer(modifier = Modifier.size(10.dp))

            Button(onClick = {
                navController.navigate(Views.Quiz.name)
            }) {
                Text("Try again")
            }
        }
    }
}