package com.example.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

class RegisterActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        auth = FirebaseAuth.getInstance()

        setContent {
            LoginTheme {
                RegisterScreen { email, password ->
                    registerUser(email, password)
                }
            }
        }
    }

    private fun registerUser(email: String, password: String) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Falha na criação de usuário: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

@Composable
fun RegisterScreen(onRegister: (String, String) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        BackGround_img()
        RegisterContent(onRegister)
    }
}

@Composable
fun RegisterContent(onRegister: (String, String) -> Unit) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var validEmail by remember { mutableStateOf(true) }
    var validPassword by remember { mutableStateOf(true) }
    var passwordsMatch by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                RegisterTitle()

                RegisterEmailField(
                    email = email,
                    isValid = validEmail,
                    onValueChange = {
                        email = it
                        validEmail = isValidEmail(it)
                    }
                )

                RegisterPasswordField(
                    password = password,
                    isValid = validPassword,
                    onValueChange = {
                        password = it
                        validPassword = isValidPassword(it)
                        passwordsMatch = confirmPassword.isEmpty() || confirmPassword == password
                    }
                )

                ConfirmPasswordField(
                    confirmPassword = confirmPassword,
                    isValid = passwordsMatch,
                    onValueChange = {
                        confirmPassword = it
                        passwordsMatch = it == password
                    }
                )

                RegisterButton {
                    val emailOk = isValidEmail(email)
                    val passOk = isValidPassword(password)
                    val matchOk = password == confirmPassword

                    validEmail = emailOk
                    validPassword = passOk
                    passwordsMatch = matchOk

                    if (emailOk && passOk && matchOk) {
                        onRegister(email, password)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterTitle() {
    Text(
        text = "Crie sua conta",
        color = Color.White,
        fontSize = 42.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        lineHeight = 48.sp,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
fun RegisterEmailField(email: String, isValid: Boolean, onValueChange: (String) -> Unit) {
    Column {
        TextField(
            value = email,
            onValueChange = onValueChange,
            label = {
                Text(
                    text = "exemplo:@gmail.com",
                    fontSize = 14.sp
                )
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Gray
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = !isValid,
        )
        if (!isValid) {
            Text(
                text = "Email inválido. Por favor, digite um email válido",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun RegisterPasswordField(password: String, isValid: Boolean, onValueChange: (String) -> Unit) {
    Column {
        TextField(
            value = password,
            onValueChange = onValueChange,
            label = {
                Text(
                    text = "Digite sua senha",
                    fontSize = 14.sp
                )
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Gray
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            isError = !isValid,
        )
        if (!isValid) {
            Text(
                text = "Senha inválida. A senha deve conter no mínimo 8 caracteres, uma letra maiúscula e um número",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun ConfirmPasswordField(confirmPassword: String, isValid: Boolean, onValueChange: (String) -> Unit) {
    Column {
        TextField(
            value = confirmPassword,
            onValueChange = onValueChange,
            label = {
                Text(
                    text = "Confirme sua senha",
                    fontSize = 14.sp
                )
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Gray
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            isError = !isValid,
        )
        if (!isValid) {
            Text(
                text = "As senhas não coincidem",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun RegisterButton(onClicked: () -> Unit) {
    Button(
        onClick = onClicked,
        modifier = Modifier.fillMaxWidth().height(60.dp).padding(horizontal = 16.dp, vertical = 2.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFA951A)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = "Cadastrar",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

fun isValidEmail(email: String): Boolean {
    val emailTemplate = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
    val template = Pattern.compile(emailTemplate)
    val matcher = template.matcher(email)
    return matcher.matches()
}

fun isValidPassword(password: String): Boolean {
    val passwordTemplate = "^(?=.*[A-Z])(?=.*\\d).{8,}\$"
    val template = Pattern.compile(passwordTemplate)
    val matcher = template.matcher(password)
    return matcher.matches()
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    LoginTheme {
        RegisterScreen { _, _ -> }
    }
}