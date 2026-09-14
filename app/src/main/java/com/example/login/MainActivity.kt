package com.example.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.login.ui.theme.LoginTheme
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        auth = FirebaseAuth.getInstance()

        setContent {
            LoginTheme {
                LoginScreen(
                    onLogin = { email, password ->
                        login(email, password)
                    },
                    onRegisterClick = {
                        val intent = Intent(this, RegisterActivity::class.java)
                        startActivity(intent)
                    }
                )
            }
        }
    }

    private fun login(email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Falha no login: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

@Composable
fun LoginScreen(onLogin: (String, String) -> Unit, onRegisterClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FA))
    ) {
        LoginContent(onLogin, onRegisterClick)
    }
}

@Composable
fun LoginContent(onLogin: (String, String) -> Unit, onRegisterClick: () -> Unit) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var validEmail by remember { mutableStateOf(true) }
    var validPassword by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Image(
            painter = painterResource(id = R.drawable.app_img),
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = "Welcome Back",
            color = Color.Black,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Text(
            text = "Login to your account",
            color = Color.Gray,
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            textAlign = TextAlign.Center
        )

        EmailField(email) {
            email = it
            validEmail = validEmail(it)
        }
        Spacer(modifier = Modifier.height(8.dp))
        PasswordField(password) {
            password = it
            validPassword = validPass(it)
        }

        Text(
            text = "Forgot Password?",
            color = Color(0xFF0F93FF),
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.End)
                .padding(vertical = 8.dp)
                .clickable {}
        )

        Spacer(modifier = Modifier.height(24.dp))

        LoginButton {
            if (validEmail(email) && validPass(password)) {
                onLogin(email, password)
            } else {
                validEmail = validEmail(email)
                validPassword = validPass(password)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
            Text(
                text = "Or sign in with",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            modifier = Modifier
                .size(48.dp)
                .clickable { },
            shape = CircleShape,
            color = Color.White,
            border = BorderStroke(1.dp, Color.LightGray)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.google_img),
                    contentDescription = "Login com Google",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        RegisterAncor(onRegisterClick)
    }
}

@Composable
fun EmailField(email: String, onValueChange: (String) -> Unit) {
    var isError by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = email,
            onValueChange = {
                onValueChange(it)
                isError = !validEmail(it)
            },
            placeholder = { Text("Email Address", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color(0xFF0F93FF),
                unfocusedBorderColor = Color.LightGray,
                errorBorderColor = Color.Red
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = isError,
            singleLine = true
        )
        if (isError) {
            Text(
                text = "Please enter a valid email",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun PasswordField(password: String, onValueChange: (String) -> Unit) {
    var isError by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = password,
            onValueChange = {
                onValueChange(it)
                isError = !validPass(it)
            },
            placeholder = { Text("Password", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color(0xFF673AB7),
                unfocusedBorderColor = Color.LightGray,
                errorBorderColor = Color.Red
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            isError = isError,
            singleLine = true
        )
        if (isError) {
            Text(
                text = "Password must be 8+ chars with a capital and a number",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun RegisterAncor(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Don't have any account? ",
            color = Color.Gray,
            fontSize = 14.sp
        )
        Text(
            text = "Signup",
            color = Color(0xFF0F93FF),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onClick() }
        )
    }
}

@Composable
fun LoginButton(onClicked: () -> Unit) {
    Button(
        onClick = onClicked,
        modifier = Modifier
            .width(150.dp)
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F93FF)),
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = "Login",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
fun validEmail(email: String): Boolean {
    val emailTemplate = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
    val template = Pattern.compile(emailTemplate)
    val matcher = template.matcher(email)
    return matcher.matches()
}

fun validPass(password: String): Boolean {
    val emailTemplate = "^(?=.*[A-Z])(?=.*\\d).{8,}\$"
    val template = Pattern.compile(emailTemplate)
    val matcher = template.matcher(password)
    return matcher.matches()
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginTheme {
        LoginScreen(onLogin = {_, _ ->}, onRegisterClick = {})
    }
}