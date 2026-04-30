package com.personalizasv.app

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator
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

    private var currentStep = 1
    private var selectedCliente: Cliente? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_pedido)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val txtStepIndicator = findViewById<TextView>(R.id.txtStepIndicator)
        val progressIndicator = findViewById<LinearProgressIndicator>(R.id.progressIndicator)
        val btnAnterior = findViewById<MaterialButton>(R.id.btnAnterior)
        val btnSiguiente = findViewById<MaterialButton>(R.id.btnSiguiente)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        val step1 = findViewById<NestedScrollView>(R.id.step1)
        val step2 = findViewById<NestedScrollView>(R.id.step2)
        val step3 = findViewById<NestedScrollView>(R.id.step3)
        val step4 = findViewById<NestedScrollView>(R.id.step4)

        val steps = listOf(step1, step2, step3, step4)

        val editBuscarTelefono = findViewById<TextInputEditText>(R.id.editBuscarTelefono)
        val cardClienteEncontrado = findViewById<MaterialCardView>(R.id.cardClienteEncontrado)
        val txtNombreCliente = findViewById<TextView>(R.id.txtNombreCliente)
        val txtTelefonoCliente = findViewById<TextView>(R.id.txtTelefonoCliente)
        val txtDireccionCliente = findViewById<TextView>(R.id.txtDireccionCliente)

        val autoCompleteProducto = findViewById<AutoCompleteTextView>(R.id.autoCompleteProducto)
        val recyclerItems = findViewById<RecyclerView>(R.id.recyclerItems)
        val txtSinItems = findViewById<TextView>(R.id.txtSinItems)

        val autoCompleteMetodoPago = findViewById<AutoCompleteTextView>(R.id.autoCompleteMetodoPago)
        val editFechaEntrega = findViewById<TextInputEditText>(R.id.editFechaEntrega)
        val editDireccionEntrega = findViewById<TextInputEditText>(R.id.editDireccionEntrega)

        val txtResumenCliente = findViewById<TextView>(R.id.txtResumenCliente)
        val txtResumenPago = findViewById<TextView>(R.id.txtResumenPago)
        val txtResumenFecha = findViewById<TextView>(R.id.txtResumenFecha)
        val txtResumenDireccion = findViewById<TextView>(R.id.txtResumenDireccion)
        val layoutResumenProductos = findViewById<LinearLayout>(R.id.layoutResumenProductos)
        val txtTotal = findViewById<TextView>(R.id.txtTotal)

        val editNotas = findViewById<TextInputEditText>(R.id.editNotas)

        btnBack.setOnClickListener { finish() }

        adapter = PedidoCartAdapter(
            onCantidadChange = { index, cantidad -> viewModel.actualizarCantidad(index, cantidad) },
            onEliminar = { index -> viewModel.eliminarItem(index) },
            onPersonalizacionChange = { index, p -> viewModel.actualizarPersonalizacion(index, p) }
        )
        recyclerItems.layoutManager = LinearLayoutManager(this)
        recyclerItems.adapter = adapter

        val metodosPago = listOf("Efectivo", "Transferencia", "Tarjeta de credito", "Tarjeta de debito", "Otro")
        autoCompleteMetodoPago.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, metodosPago))
        autoCompleteMetodoPago.setOnItemClickListener { _, _, position, _ ->
            viewModel.onFieldChange("metodoPago", metodosPago[position])
        }

        val calendar = Calendar.getInstance()
        editFechaEntrega.setOnClickListener {
            DatePickerDialog(this, { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                viewModel.setFechaEntrega(calendar.timeInMillis)
                editFechaEntrega.setText(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        editBuscarTelefono.doOnTextChanged { text -> viewModel.buscarClientePorTelefono(text) }
        editDireccionEntrega.doOnTextChanged { text -> viewModel.onFieldChange("direccion", text) }
        editNotas.doOnTextChanged { text -> viewModel.onFieldChange("notas", text) }

        fun updateStep(step: Int) {
            currentStep = step
            steps.forEachIndexed { i, s -> s.visibility = if (i + 1 == step) View.VISIBLE else View.GONE }
            txtStepIndicator.text = "Paso $step de 4"
            progressIndicator.progress = step * 25
            btnAnterior.visibility = if (step > 1) View.VISIBLE else View.GONE
            btnSiguiente.text = if (step == 4) "Registrar Pedido" else "Siguiente"
        }

        btnAnterior.setOnClickListener {
            if (currentStep > 1) updateStep(currentStep - 1)
        }

        btnSiguiente.setOnClickListener {
            when (currentStep) {
                1 -> {
                    if (selectedCliente == null) {
                        Toast.makeText(this, "Debe seleccionar un cliente", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    updateStep(2)
                }
                2 -> {
                    if (viewModel.formState.value.items.isEmpty()) {
                        Toast.makeText(this, "Debe agregar al menos un producto", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    updateStep(3)
                }
                3 -> {
                    if (viewModel.formState.value.metodoPago.isBlank()) {
                        Toast.makeText(this, "Debe seleccionar un metodo de pago", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    if (viewModel.formState.value.fechaEntregaEstimada == 0L) {
                        Toast.makeText(this, "Debe seleccionar fecha de entrega", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    updateStep(4)
                }
                4 -> {
                    viewModel.registrarPedido()
                }
            }
        }

        updateStep(1)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.productos.collect { productos ->
                        val nombres = productos.map { it.nombre }
                        autoCompleteProducto.setAdapter(ArrayAdapter(this@RegistrarPedidoActivity, android.R.layout.simple_dropdown_item_1line, nombres))
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
                            txtTelefonoCliente.text = cliente.telefono
                            txtDireccionCliente.text = cliente.direccion.ifBlank { "Sin dirección" }
                            selectedCliente = cliente
                            viewModel.seleccionarCliente(cliente)
                        } else {
                            cardClienteEncontrado.visibility = View.GONE
                            selectedCliente = null
                        }
                    }
                }

                launch {
                    viewModel.formState.collect { form ->
                        adapter.submitList(form.items)
                        txtSinItems.visibility = if (form.items.isEmpty()) View.VISIBLE else View.GONE
                        txtTotal.text = "$${"%.2f".format(viewModel.totalPedido)}"

                        if (currentStep == 4) {
                            txtResumenCliente.text = form.cliente?.nombreCompleto ?: "N/A"
                            txtResumenPago.text = form.metodoPago.ifBlank { "N/A" }
                            txtResumenFecha.text = if (form.fechaEntregaEstimada > 0) {
                                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(form.fechaEntregaEstimada)
                            } else "N/A"
                            txtResumenDireccion.text = form.direccionEntrega.ifBlank { "N/A" }

                            layoutResumenProductos.removeAllViews()
                            form.items.forEach { item ->
                                val row = TextView(this@RegistrarPedidoActivity).apply {
                                    text = "• ${item.producto.nombre} x${item.cantidad} - $${"%.2f".format(item.subtotal)}"
                                    textSize = 14f
                                    setTextColor(getColor(R.color.text_primary))
                                    setPadding(0, 4, 0, 4)
                                }
                                layoutResumenProductos.addView(row)
                            }
                        }
                    }
                }

                launch {
                    viewModel.uiState.collect { state ->
                        when (state) {
                            is PedidoUiState.Idle -> {
                                progressBar.visibility = View.GONE
                                btnSiguiente.isEnabled = true
                            }
                            is PedidoUiState.Loading -> {
                                progressBar.visibility = View.VISIBLE
                                btnSiguiente.isEnabled = false
                            }
                            is PedidoUiState.Success -> {
                                progressBar.visibility = View.GONE
                                btnSiguiente.isEnabled = true
                                Toast.makeText(this@RegistrarPedidoActivity, state.message, Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            is PedidoUiState.Error -> {
                                progressBar.visibility = View.GONE
                                btnSiguiente.isEnabled = true
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
