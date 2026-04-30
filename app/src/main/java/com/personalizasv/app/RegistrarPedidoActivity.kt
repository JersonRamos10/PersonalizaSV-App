package com.personalizasv.app

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.personalizasv.app.data.models.Cliente
import com.personalizasv.app.ui.pedido.PedidoCartAdapter
import com.personalizasv.app.ui.pedido.PedidoUiState
import com.personalizasv.app.ui.pedido.RegistrarPedidoViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RegistrarPedidoActivity : AppCompatActivity() {

    private val viewModel: RegistrarPedidoViewModel by viewModels()

    private lateinit var adapter: PedidoCartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_pedido)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val editBuscarTelefono = findViewById<TextInputEditText>(R.id.editBuscarTelefono)
        val cardClienteEncontrado = findViewById<MaterialCardView>(R.id.cardClienteEncontrado)
        val txtNombreCliente = findViewById<TextView>(R.id.txtNombreCliente)
        val txtDireccionCliente = findViewById<TextView>(R.id.txtDireccionCliente)
        val autoCompleteProducto = findViewById<AutoCompleteTextView>(R.id.autoCompleteProducto)
        val recyclerItems = findViewById<RecyclerView>(R.id.recyclerItems)
        val txtSinItems = findViewById<TextView>(R.id.txtSinItems)
        val autoCompleteMetodoPago = findViewById<AutoCompleteTextView>(R.id.autoCompleteMetodoPago)
        val editFechaEntrega = findViewById<TextInputEditText>(R.id.editFechaEntrega)
        val editDireccionEntrega = findViewById<TextInputEditText>(R.id.editDireccionEntrega)
        val editNotas = findViewById<TextInputEditText>(R.id.editNotas)
        val txtTotal = findViewById<TextView>(R.id.txtTotal)
        val btnRegistrar = findViewById<MaterialButton>(R.id.btnRegistrarPedido)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        btnBack.setOnClickListener { finish() }

        adapter = PedidoCartAdapter(
            onCantidadChange = { index, cantidad ->
                viewModel.actualizarCantidad(index, cantidad)
            },
            onEliminar = { index ->
                viewModel.eliminarItem(index)
            },
            onPersonalizacionChange = { index, personalizacion ->
                viewModel.actualizarPersonalizacion(index, personalizacion)
            }
        )
        recyclerItems.layoutManager = LinearLayoutManager(this)
        recyclerItems.adapter = adapter

        val metodosPago = listOf("Efectivo", "Transferencia", "Tarjeta de credito", "Tarjeta de debito", "Otro")
        autoCompleteMetodoPago.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, metodosPago)
        )
        autoCompleteMetodoPago.setOnItemClickListener { _, _, position, _ ->
            viewModel.onFieldChange("metodoPago", metodosPago[position])
        }

        val calendar = Calendar.getInstance()
        editFechaEntrega.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    viewModel.setFechaEntrega(calendar.timeInMillis)
                    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    editFechaEntrega.setText(format.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        editBuscarTelefono.doOnTextChanged { text ->
            viewModel.buscarClientePorTelefono(text)
        }

        editDireccionEntrega.doOnTextChanged { text ->
            viewModel.onFieldChange("direccion", text)
        }

        editNotas.doOnTextChanged { text ->
            viewModel.onFieldChange("notas", text)
        }

        btnRegistrar.setOnClickListener {
            viewModel.registrarPedido()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.productos.collect { productos ->
                        val nombres = productos.map { it.nombre }
                        autoCompleteProducto.setAdapter(
                            ArrayAdapter(this@RegistrarPedidoActivity, android.R.layout.simple_dropdown_item_1line, nombres)
                        )
                        autoCompleteProducto.setOnItemClickListener { _, _, position, _ ->
                            viewModel.agregarProducto(productos[position])
                            autoCompleteProducto.setText("")
                        }
                    }
                }

                launch {
                    viewModel.clienteBusqueda.collect { cliente ->
                        if (cliente != null) {
                            cardClienteEncontrado.visibility = View.VISIBLE
                            txtNombreCliente.text = cliente.nombreCompleto
                            txtDireccionCliente.text = cliente.direccion.ifBlank { "Sin dirección" }
                            viewModel.seleccionarCliente(cliente)
                        } else {
                            cardClienteEncontrado.visibility = View.GONE
                        }
                    }
                }

                launch {
                    viewModel.formState.collect { form ->
                        adapter.submitList(form.items)
                        txtSinItems.visibility = if (form.items.isEmpty()) View.VISIBLE else View.GONE
                        txtTotal.text = "$${"%.2f".format(viewModel.totalPedido)}"
                    }
                }

                launch {
                    viewModel.uiState.collect { state ->
                        when (state) {
                            is PedidoUiState.Idle -> {
                                progressBar.visibility = View.GONE
                                btnRegistrar.isEnabled = true
                                btnRegistrar.text = "Registrar Pedido"
                            }
                            is PedidoUiState.Loading -> {
                                progressBar.visibility = View.VISIBLE
                                btnRegistrar.isEnabled = false
                                btnRegistrar.text = "Registrando..."
                            }
                            is PedidoUiState.Success -> {
                                progressBar.visibility = View.GONE
                                btnRegistrar.isEnabled = true
                                btnRegistrar.text = "Registrar Pedido"
                                Toast.makeText(this@RegistrarPedidoActivity, state.message, Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            is PedidoUiState.Error -> {
                                progressBar.visibility = View.GONE
                                btnRegistrar.isEnabled = true
                                btnRegistrar.text = "Registrar Pedido"
                                Toast.makeText(this@RegistrarPedidoActivity, state.message, Toast.LENGTH_LONG).show()
                                viewModel.resetState()
                            }
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

private fun TextInputEditText.doOnTextChanged(action: (String) -> Unit) {
    addTextChangedListener(object : android.text.TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            action(s?.toString() ?: "")
        }
        override fun afterTextChanged(s: android.text.Editable?) {}
    })
}
