package com.personalizasv.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.personalizasv.app.data.remote.FirebaseConfig

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val btnRegistrarCliente = findViewById<Button>(R.id.editRegistrar)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        btnRegistrarCliente.setOnClickListener {
            startActivity(Intent(this, RegistrarClienteActivity::class.java))
        }

        btnLogout.setOnClickListener {
            FirebaseConfig.auth.signOut()
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}