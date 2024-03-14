package com.trulyao.northlearn.views.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.trulyao.northlearn.views.Views

@Composable
fun QuizSplash(navController: NavController) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Column {
            Text(text = "Guess the animal", fontSize = 40.sp, fontWeight = FontWeight(500))

            Spacer(modifier = Modifier.size(12.dp))

            Text(
                text = "You will be presented with a number of random images every round (with no time limit) and you have to enter the name of the animal in each image displayed (no case sensitivity), every correct guess will land you get 10 points, do you understand?",
                color = Color.Gray,
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Internet connection required",
                color = MaterialTheme.colorScheme.outline,
                fontSize = 14.sp
            )

            Button(onClick = { navController.navigate(Views.Quiz.name) { popUpTo(Views.Home.name) } }) {
                Text(text = "Yes, start quiz")
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun QuizSplashPreview() {
    val navController = rememberNavController()

    QuizSplash(navController)
}