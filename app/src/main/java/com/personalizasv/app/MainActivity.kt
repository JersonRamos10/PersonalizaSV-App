package com.personalizasv.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.personalizasv.app.data.remote.FirebaseConfig

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseConfig.auth

        if (auth.currentUser != null) {
            navigateToDashboard()
            return
        }

        val editEmail = findViewById<EditText>(R.id.editEmail)
        val editPassword = findViewById<EditText>(R.id.editPassword)
        val btnIngresar = findViewById<Button>(R.id.editIngresar)

        btnIngresar.setOnClickListener {
            val email = editEmail.text.toString().trim()
            val password = editPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                loginWithEmail(email, password)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loginWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show()
                    navigateToDashboard()
                } else {
                    val message = when (task.exception) {
                        is FirebaseAuthInvalidUserException -> "Usuario no registrado"
                        is FirebaseAuthInvalidCredentialsException -> "Contraseña incorrecta"
                        else -> "Error al iniciar sesión: ${task.exception?.message}"
                    }
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun navigateToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}