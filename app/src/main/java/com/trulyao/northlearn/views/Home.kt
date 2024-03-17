package com.trulyao.northlearn.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.trulyao.northlearn.models.findByID
import com.trulyao.northlearn.utils.Store
import com.trulyao.northlearn.utils.StoreKey
import kotlinx.coroutines.launch

@Composable
fun Home(navController: NavController, userID: Int?) {
    if (userID == null) return;

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val user = findByID(userID)

    fun logout() {
        coroutineScope.launch {
            Store.set(context, key = StoreKey.User, value = null)
        }
    }


    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(
            text = "Tools",
            fontWeight = FontWeight(700),
            fontSize = 40.sp,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 22.dp)
        )

        Spacer(modifier = Modifier.size(20.dp))

        NavButton(
            title = "My Notes",
            destination = "${Views.Notes.name}/(null)",
            navController = navController
        )
        NavButton(
            title = "Quiz",
            destination = Views.QuizSplash.name,
            navController = navController
        )
        NavButton(
            title = "Calculator",
            destination = Views.Calculator.name,
            navController = navController
        )

        Spacer(modifier = Modifier.size(28.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            if (user.isPresent) {
                Text(text = "Signed in as ${user.get().username}")
            }

            Spacer(modifier = Modifier.size(2.dp))

            TextButton(
                onClick = { logout() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign Out")
            }
        }
    }
}

@Composable
fun NavButton(navController: NavController, title: String, destination: String) {
    Surface(
        onClick = { navController.navigate(destination) },
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 22.dp)
                .wrapContentHeight()
        ) {
            Text(
                text = title,
                modifier = Modifier
                    .wrapContentHeight()
            )

            Icon(Icons.Default.KeyboardArrowRight, "Go to $title")
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun HomePreview() {
    val navController = rememberNavController()
    Home(navController = navController, userID = 1)
}