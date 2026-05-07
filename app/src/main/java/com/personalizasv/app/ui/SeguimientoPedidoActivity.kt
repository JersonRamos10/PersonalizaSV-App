package com.personalizasv.app.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.personalizasv.app.R
import com.personalizasv.app.data.models.Pedido
import com.personalizasv.app.ui.seguimiento.SeguimientoAdapter
import com.personalizasv.app.ui.seguimiento.SeguimientoUiState
import com.personalizasv.app.ui.seguimiento.SeguimientoViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class SeguimientoPedidoActivity : AppCompatActivity() {

    private val viewModel: SeguimientoViewModel by viewModels()
    private lateinit var adapter: SeguimientoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seguimiento_pedido)

        val btnBack = findViewById<ImageButton>(R.id.btnBack4)
        val editBuscar = findViewById<TextInputEditText>(R.id.editBuscarPedido)
        val btnBuscar = findViewById<MaterialButton>(R.id.btnBuscar)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val txtEmpty = findViewById<TextView>(R.id.txtEmpty)
        val recyclerPedidos = findViewById<RecyclerView>(R.id.recyclerPedidos)
        val cardDetalle = findViewById<MaterialCardView>(R.id.cardDetalle)

        val txtDetalleEstado = findViewById<TextView>(R.id.txtDetalleEstado)
        val txtDetalleFecha = findViewById<TextView>(R.id.txtDetalleFecha)
        val txtDetalleEntrega = findViewById<TextView>(R.id.txtDetalleEntrega)
        val txtDetallePago = findViewById<TextView>(R.id.txtDetallePago)
        val txtDetalleDireccion = findViewById<TextView>(R.id.txtDetalleDireccion)
        val txtDetalleTotal = findViewById<TextView>(R.id.txtDetalleTotal)
        val layoutDetalleProductos = findViewById<LinearLayout>(R.id.layoutDetalleProductos)
        val layoutNotas = findViewById<TextInputLayout>(R.id.layoutNotas)
        val txtDetalleNotas = findViewById<TextInputEditText>(R.id.txtDetalleNotas)

        btnBack.setOnClickListener { finish() }

        adapter = SeguimientoAdapter { pedido ->
            viewModel.seleccionarPedido(pedido)
        }
        recyclerPedidos.layoutManager = LinearLayoutManager(this)
        recyclerPedidos.adapter = adapter

        editBuscar.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.filtrarPedidos(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        btnBuscar.visibility = View.GONE

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.pedidos.collect { lista ->
                        adapter.submitList(lista)
                        txtEmpty.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
                    }
                }

                launch {
                    viewModel.pedidoSeleccionado.collect { pedido ->
                        if (pedido != null) {
                            cardDetalle.visibility = View.VISIBLE
                            txtDetalleEstado.text = formatearEstado(pedido.estado)
                            txtDetalleEstado.setTextColor(getColor(estadoColor(pedido.estado)))
                            txtDetalleFecha.text = formatearFecha(pedido.fechaPedido)
                            txtDetalleEntrega.text = if (pedido.fechaEntregaEstimada > 0) {
                                formatearFecha(pedido.fechaEntregaEstimada)
                            } else "No definida"
                            txtDetallePago.text = pedido.metodoPago.ifBlank { "N/A" }
                            txtDetalleDireccion.text = pedido.direccionEntrega.ifBlank { "N/A" }
                            txtDetalleTotal.text = "$${"%.2f".format(pedido.totalPedido)}"

                            layoutDetalleProductos.removeAllViews()
                            pedido.productos.forEach { item ->
                                val row = TextView(this@SeguimientoPedidoActivity).apply {
                                    text = "• ${item.nombreProducto} x${item.cantidad} - $${"%.2f".format(item.precioUnitario * item.cantidad)}"
                                    textSize = 13f
                                    setTextColor(getColor(R.color.text_primary))
                                    setPadding(0, 4, 0, 4)
                                }
                                layoutDetalleProductos.addView(row)

                                val p = item.personalizacion
                                val detalles = listOfNotNull(
                                    "Talla: ${p.talla}".takeIf { p.talla.isNotBlank() },
                                    "Color: ${p.color}".takeIf { p.color.isNotBlank() },
                                    "Texto: ${p.textoPersonalizado}".takeIf { p.textoPersonalizado.isNotBlank() }
                                )
                                if (detalles.isNotEmpty()) {
                                    val detalle = TextView(this@SeguimientoPedidoActivity).apply {
                                        text = "  ${detalles.joinToString(", ")}"
                                        textSize = 12f
                                        setTextColor(getColor(R.color.text_secondary))
                                        setPadding(0, 0, 0, 8)
                                    }
                                    layoutDetalleProductos.addView(detalle)
                                }
                            }

                            if (pedido.notasAdicionales.isNotBlank()) {
                                layoutNotas.visibility = View.VISIBLE
                                txtDetalleNotas.setText(pedido.notasAdicionales)
                            } else {
                                layoutNotas.visibility = View.GONE
                            }
                        } else {
                            cardDetalle.visibility = View.GONE
                        }
                    }
                }

                launch {
                    viewModel.uiState.collect { state ->
                        when (state) {
                            is SeguimientoUiState.Loading -> {
                                progressBar.visibility = View.VISIBLE
                            }
                            is SeguimientoUiState.Success -> {
                                progressBar.visibility = View.GONE
                                Toast.makeText(this@SeguimientoPedidoActivity, state.message, Toast.LENGTH_SHORT).show()
                                viewModel.resetState()
                            }
                            is SeguimientoUiState.Error -> {
                                progressBar.visibility = View.GONE
                                Toast.makeText(this@SeguimientoPedidoActivity, state.message, Toast.LENGTH_LONG).show()
                                viewModel.resetState()
                            }
                            else -> progressBar.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun formatearEstado(estado: String): String = when (estado) {
        "pendiente" -> "Pendiente"
        "diseno_aprobado" -> "Diseño Aprobado"
        "en_produccion" -> "En Producción"
        "listo_entrega" -> "Listo para Entrega"
        "entregado" -> "Entregado"
        "cancelado" -> "Cancelado"
        else -> estado.replace("_", " ").replaceFirstChar { it.uppercase() }
    }

    private fun estadoColor(estado: String): Int = when (estado) {
        "pendiente" -> R.color.warning
        "diseno_aprobado" -> R.color.info
        "en_produccion" -> R.color.primary
        "listo_entrega" -> R.color.success
        "entregado" -> R.color.success
        "cancelado" -> R.color.error
        else -> R.color.text_secondary
    }

    private fun formatearFecha(timestamp: Long): String {
        return if (timestamp > 0) {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(timestamp)
        } else "N/A"
    }
}
