package com.trulyao.northlearn.views.quiz

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.trulyao.northlearn.models.API_URL
import com.trulyao.northlearn.models.QuizViewModel
import com.trulyao.northlearn.models.QuizViewState
import com.trulyao.northlearn.views.Views
import java.util.Locale

@Composable
fun Quiz(navController: NavController, quizViewModel: QuizViewModel) {
    val context = LocalContext.current

    // Run everytime this view is rendered
    LaunchedEffect(true) {
        quizViewModel.startRound()
    }

    when (quizViewModel.uiState) {
        is QuizViewState.Success -> QuizScreen(
            context = context,
            navController = navController,
            quizViewModel
        )

        is QuizViewState.Loading -> LoadingScreen()
        is QuizViewState.Error -> ErrorScreen(retry = { quizViewModel.load(context) })
    }
}

@Composable
fun QuizScreen(context: Context, navController: NavController, quizViewModel: QuizViewModel) {
    val state = quizViewModel.uiState as QuizViewState.Success

    if (state.currentRound.questions.isEmpty()) return LoadingScreen()

    val currentQuestion by remember {
        derivedStateOf { state.currentRound.questions[state.currentRound.currentQuestion.value] }
    }

    val isLastQuestion by remember {
        derivedStateOf { state.currentRound.currentQuestion.value == (state.currentRound.questions.size - 1) }
    }

    fun next(advance: Boolean = true, force: Boolean = false) {
        if (state.currentRound.currentQuestion.value == (state.currentRound.questions.size - 1) && !force) return;
        if (
            currentQuestion.answer.value.trim()
                .lowercase(Locale.ROOT) == currentQuestion.animal.name
            && currentQuestion.hasBeenMarked.value.not()
        ) {
            quizViewModel.addPoints()
            currentQuestion.hasBeenMarked.value = true
        }

        if (advance) state.currentRound.currentQuestion.value += 1
    }

    fun previous() {
        state.currentRound.currentQuestion.value -= 1
    }

    fun finish() {
        next(advance = false, force = true)

        // Navigate to the result screen and by "replacing" the current page (i.e. rewriting history)
        navController.navigate(Views.QuizScore.name) {
            popUpTo(Views.Home.name)
        }
    }

    val listener = object : ImageRequest.Listener {
        override fun onError(request: ImageRequest, result: ErrorResult) {
            super.onError(request, result)
        }

        override fun onSuccess(request: ImageRequest, result: SuccessResult) {
            super.onSuccess(request, result)
        }
    }

    val imageSrc = "${API_URL}/images/${currentQuestion.animal.filename}"
    val imageRequest = ImageRequest.Builder(context = context)
        .data(imageSrc)
        .listener(listener)
        .memoryCacheKey(imageSrc)
        .diskCacheKey(imageSrc)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .build()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 42.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(vertical = 10.dp, horizontal = 16.dp)
            ) {
                Text(
                    text = "${state.currentRound.score.value} points",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight(400),
                    fontSize = 18.sp,
                )
            }
        }

        Column {
            AsyncImage(
                model = imageRequest,
                contentDescription = currentQuestion.animal.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(380.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.size(20.dp))

            // Answer
            TextField(
                value = currentQuestion.answer.value,
                onValueChange = { currentQuestion.answer.value = it },
                placeholder = { Text("Enter your guess here...") },
                modifier = Modifier
                    .width(380.dp)
                    .clip(RoundedCornerShape(6.dp)),
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    autoCorrect = false,
                    capitalization = KeyboardCapitalization.None,
                    imeAction = ImeAction.Done
                )
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
        ) {
            // Previous question
            Button(
                onClick = { previous() },
                enabled = state.currentRound.currentQuestion.value > 0,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(80.dp)
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Previous question",
                    modifier = Modifier.size(32.dp)
                )
            }

            // Question status
            Text(text = "${state.currentRound.currentQuestion.value + 1} out of ${state.currentRound.questions.size}")

            // Next question or submit
            Button(
                onClick = { if (isLastQuestion) finish() else next() }, // If on last question, submit on next press
                enabled = currentQuestion.answer.value.isNotEmpty(),
                modifier = Modifier
                    .clip(CircleShape)
                    .size(80.dp)
            ) {
                Icon(
                    if (isLastQuestion) Icons.Default.Done else Icons.Default.ArrowForward,
                    contentDescription = if (isLastQuestion) "Finish quiz" else "Previous question",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
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
fun ErrorScreen(retry: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color.Red)
            .fillMaxSize()
    ) {
        Text(text = "Something went wrong, please restart the app and try again.")

        Spacer(modifier = Modifier.size(10.dp))

        Button(
            onClick = { retry() },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            Text("Retry", color = Color.Red)
        }
    }
}