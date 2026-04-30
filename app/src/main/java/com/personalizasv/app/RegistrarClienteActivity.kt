package com.personalizasv.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.personalizasv.app.ui.cliente.ClienteUiState
import com.personalizasv.app.ui.cliente.RegistrarClienteViewModel
import kotlinx.coroutines.launch

class RegistrarClienteActivity : AppCompatActivity() {

    private val viewModel: RegistrarClienteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_cliente)

        val editNombre = findViewById<EditText>(R.id.editNombre)
        val editTelefono = findViewById<EditText>(R.id.editTelefono)
        val editCorreo = findViewById<EditText>(R.id.editCorreo)
        val editDireccion = findViewById<EditText>(R.id.editDireccion)
        val editNotas = findViewById<EditText>(R.id.editNotas)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrarCliente)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        editNombre.doOnTextChanged { viewModel.onFieldChange("nombre", it) }
        editTelefono.doOnTextChanged { viewModel.onFieldChange("telefono", it) }
        editCorreo.doOnTextChanged { viewModel.onFieldChange("correo", it) }
        editDireccion.doOnTextChanged { viewModel.onFieldChange("direccion", it) }
        editNotas.doOnTextChanged { viewModel.onFieldChange("notas", it) }

        btnRegistrar.setOnClickListener {
            viewModel.registrarCliente()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is ClienteUiState.Idle -> {
                            progressBar.visibility = android.view.View.GONE
                            btnRegistrar.isEnabled = true
                        }
                        is ClienteUiState.Loading -> {
                            progressBar.visibility = android.view.View.VISIBLE
                            btnRegistrar.isEnabled = false
                        }
                        is ClienteUiState.Success -> {
                            progressBar.visibility = android.view.View.GONE
                            btnRegistrar.isEnabled = true
                            Toast.makeText(this@RegistrarClienteActivity, state.message, Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        is ClienteUiState.Error -> {
                            progressBar.visibility = android.view.View.GONE
                            btnRegistrar.isEnabled = true
                            Toast.makeText(this@RegistrarClienteActivity, state.message, Toast.LENGTH_LONG).show()
                            viewModel.resetState()
                        }
                    }
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}

private fun EditText.doOnTextChanged(action: (String) -> Unit) {
    addTextChangedListener(object : android.text.TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            action(s?.toString() ?: "")
        }
        override fun afterTextChanged(s: android.text.Editable?) {}
    })
}
