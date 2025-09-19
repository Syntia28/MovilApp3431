package com.upn.movilapp3431

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.upn.movilapp3431.ui.theme.MovilApp3431Theme
import androidx.core.content.edit
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.firestore
import com.upn.movilapp3431.entities.Contact
import com.upn.movilapp3431.entities.User

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val preferences = getSharedPreferences("com.upn.movilapp3431", MODE_PRIVATE)
        val estaLogueado = preferences.getBoolean("ESTA_LOGUEADO", false)

        setContent {
            MovilApp3431Theme {

                val context = LocalContext.current

                if (estaLogueado) {
                    val intent = Intent(context, FirebaseRealtimeDatabaseActivity::class.java)
                    context.startActivity(intent)
                    finish()
                }

                var username by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                var showAlert by remember { mutableStateOf(false) }
                var alertMessage by remember { mutableStateOf("") }
                var alertTitle by remember { mutableStateOf("") }
                var isLoading by remember { mutableStateOf(false) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    LaunchedEffect(Unit) {
                        val usernamePref = preferences.getString("USERNAME", null)
                        if (usernamePref != null) {
                            username = usernamePref
                        }
                    }

                    if (showAlert) {
                        AlertDialog(
                            onDismissRequest = { showAlert = false },
                            title = { Text(alertTitle) },
                            text = { Text(alertMessage) },
                            confirmButton = {
                                TextButton (onClick = { showAlert = false }) {
                                    Text("OK")
                                }
                            }
                        )
                    }

                    Column(
                        modifier = Modifier.padding(innerPadding)
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Inicio de Sesión", style = MaterialTheme.typography.headlineMedium)

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Email") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            enabled = !isLoading
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = PasswordVisualTransformation(),
                            label = { Text("Contraseña") },
                            enabled = !isLoading
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (username.isBlank() || password.isBlank()) {
                                    alertTitle = "Error"
                                    alertMessage = "Por favor, completa todos los campos"
                                    showAlert = true
                                    return@Button
                                }

                                if (password.length < 6) {
                                    alertTitle = "Error"
                                    alertMessage = "La contraseña debe tener al menos 6 caracteres"
                                    showAlert = true
                                    return@Button
                                }

                                isLoading = true
                                val auth = Firebase.auth

                                auth.signInWithEmailAndPassword(username, password)
                                    .addOnSuccessListener { task ->
                                        Log.i("MAIN_APP", "Usuario logueado ${task.user?.uid}")

                                        // Guardar en shared preferences
                                        val editor = preferences.edit()
                                        editor.putBoolean("ESTA_LOGUEADO", true)
                                        editor.putString("USERNAME", username)
                                        editor.apply()

                                        alertTitle = "Éxito"
                                        alertMessage = "Usuario logueado correctamente"
                                        isLoading = false

                                        showAlert = true

                                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                            val intent = Intent(context, FirebaseRealtimeDatabaseActivity::class.java)
                                            context.startActivity(intent)
                                            finish()
                                        }, 1000)
                                    }
                                    .addOnFailureListener { error ->
                                        Log.e("MAIN_APP", "Error al loguear el usuario", error)
                                        isLoading = false
                                        alertTitle = "Error"
                                        alertMessage = when {
                                            error.message?.contains("user-not-found") == true -> "Usuario no encontrado"
                                            error.message?.contains("wrong-password") == true -> "Contraseña incorrecta"
                                            error.message?.contains("invalid-email") == true -> "Email inválido"
                                            error.message?.contains("too-many-requests") == true -> "Demasiados intentos fallidos"
                                            error.message?.contains("invalid-credential") == true -> "Credenciales inválidas"
                                            else -> "Usuario no válido o credenciales incorrectas"
                                        }
                                        showAlert = true
                                    }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading
                        ) {
                            Text(if (isLoading) "Iniciando sesión..." else "Ingresar")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row {
                            Text(
                                text = "¿No tienes cuenta? ",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            TextButton(
                                onClick = {
                                    val intent = Intent(context, RegisterActivity::class.java)
                                    context.startActivity(intent)
                                    finish()
                                },
                                enabled = !isLoading
                            ) {
                                Text("Registrarse")
                            }
                        }
                    }
                }
            }
        }
    }
}