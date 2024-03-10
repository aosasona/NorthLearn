package com.trulyao.northlearn.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trulyao.northlearn.components.RoundedButton
import com.trulyao.northlearn.components.RoundedInput
import com.trulyao.northlearn.models.findByUsername
import com.trulyao.northlearn.utils.Alert
import com.trulyao.northlearn.utils.Store
import com.trulyao.northlearn.utils.StoreKey
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }


    val enableSubmission by remember {
        derivedStateOf {
            username.length >= 2 && password.length >= 6
        }
    }

    suspend fun handleLogin() {
        try {
            loading = true
            val optUser = findByUsername(username)

            if (optUser.isEmpty) {
                throw Exception("User not found!")
            }

            val user = optUser.get()
            if (user.password != password) {
                throw Exception("Invalid credentials provided, please try again!")
            }

            Store.set(context, key = StoreKey.User, value = user.id)

        } catch (e: Exception) {
            Alert.show(context, message = e.localizedMessage!!, alertType = Alert.AlertType.Error)
        } finally {
            loading = false
        }
    }

    fun login() {
        coroutineScope.launch {
            handleLogin()
        }
    }


    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxSize(),
    ) {
        Text(text = "Welcome back", fontSize = 38.sp, fontWeight = FontWeight(700))

        Spacer(modifier = Modifier.size(6.dp))

        Text(
            text = "Sign in to your account to continue",
            fontSize = 16.sp,
            fontWeight = FontWeight(400),
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.size(40.dp))

        Column {
            RoundedInput(
                value = username,
                onChange = { username = it },
                placeholder = { Text("Username") },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = false
                )
            )

            Spacer(modifier = Modifier.size(12.dp))

            RoundedInput(
                value = password,
                onChange = { password = it },
                placeholder = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            )
        }

        Spacer(modifier = Modifier.size(40.dp))


        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            RoundedButton(onClick = { login() }, enabled = !loading && enableSubmission) {
                Text(text = "Sign In")
            }

            Spacer(modifier = Modifier.size(24.dp))

            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(24.dp)
                        .padding(0.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginViewPreview() {
    Login()
}