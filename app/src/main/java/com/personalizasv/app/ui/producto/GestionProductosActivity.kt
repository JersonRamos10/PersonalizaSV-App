package com.personalizasv.app.ui.producto

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.personalizasv.app.R
import kotlinx.coroutines.launch

class GestionProductosActivity : AppCompatActivity() {

    private lateinit var viewModel: GestionProductosViewModel
    private lateinit var adapter: ProductoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_productos)

        viewModel = GestionProductosViewModel()

        val recycler = findViewById<RecyclerView>(R.id.recyclerProductos)
        val editBuscar = findViewById<TextInputEditText>(R.id.editBuscarProducto)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val txtEmpty = findViewById<TextView>(R.id.txtEmpty)
        val fab = findViewById<FloatingActionButton>(R.id.fabAgregarProducto)

        adapter = ProductoAdapter(
            onEditar = { producto ->
                val intent = Intent(this, EditarProductoActivity::class.java)
                intent.putExtra("producto_id", producto.id)
                intent.putExtra("producto_nombre", producto.nombre)
                intent.putExtra("producto_categoria", producto.categoria)
                intent.putExtra("producto_precio", producto.precioBase)
                intent.putExtra("producto_stock", producto.stock)
                intent.putExtra("producto_tiempo", producto.tiempoProduccionDias)
                intent.putExtra("producto_imagen", producto.imagenUrl)
                intent.putExtra("producto_personalizacion", producto.opcionesPersonalizacion.joinToString(","))
                intent.putExtra("producto_activo", producto.activo)
                startActivity(intent)
            },
            onActivar = { id -> viewModel.activarProducto(id) },
            onDesactivar = { id -> viewModel.desactivarProducto(id) }
        )

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        fab.setOnClickListener {
            startActivity(Intent(this, EditarProductoActivity::class.java))
        }

        editBuscar.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onBusquedaChange(s.toString())
                actualizarLista()
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is GestionProductosUiState.Loading -> progressBar.visibility = View.VISIBLE
                        is GestionProductosUiState.Success -> {
                            progressBar.visibility = View.GONE
                            Toast.makeText(this@GestionProductosActivity, state.message, Toast.LENGTH_SHORT).show()
                            viewModel.resetState()
                        }
                        is GestionProductosUiState.Error -> {
                            progressBar.visibility = View.GONE
                            Toast.makeText(this@GestionProductosActivity, state.message, Toast.LENGTH_LONG).show()
                            viewModel.resetState()
                        }
                        else -> progressBar.visibility = View.GONE
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.productos.collect {
                    actualizarLista()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.cargarProductos()
    }

    private fun actualizarLista() {
        val lista = viewModel.productosFiltrados()
        adapter.submitList(lista)
        findViewById<TextView>(R.id.txtEmpty).visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
    }
}
