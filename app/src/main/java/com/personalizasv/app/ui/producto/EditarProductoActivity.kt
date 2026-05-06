package com.personalizasv.app.ui.producto

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.personalizasv.app.R
import com.personalizasv.app.data.models.Producto
import com.personalizasv.app.data.repositories.ProductoRepositoryImpl
import kotlinx.coroutines.launch

class EditarProductoActivity : AppCompatActivity() {

    private val repository = ProductoRepositoryImpl()
    private var productoId: String = ""
    private var esEdicion: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_producto)

        val editNombre = findViewById<TextInputEditText>(R.id.editNombre)
        val editCategoria = findViewById<TextInputEditText>(R.id.editCategoria)
        val editPrecio = findViewById<TextInputEditText>(R.id.editPrecio)
        val editStock = findViewById<TextInputEditText>(R.id.editStock)
        val editTiempo = findViewById<TextInputEditText>(R.id.editTiempoProduccion)
        val editImagen = findViewById<TextInputEditText>(R.id.editImagenUrl)
        val editPersonalizacion = findViewById<TextInputEditText>(R.id.editPersonalizacion)
        val btnGuardar = findViewById<MaterialButton>(R.id.btnGuardarProducto)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val txtTitulo = findViewById<android.widget.TextView>(R.id.txtTitulo)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        productoId = intent.getStringExtra("producto_id") ?: ""
        esEdicion = productoId.isNotBlank()

        if (esEdicion) {
            txtTitulo.text = "Editar Producto"
            btnGuardar.text = "Actualizar Producto"
            editNombre.setText(intent.getStringExtra("producto_nombre") ?: "")
            editCategoria.setText(intent.getStringExtra("producto_categoria") ?: "")
            editPrecio.setText((intent.getDoubleExtra("producto_precio", 0.0)).toString())
            editStock.setText((intent.getIntExtra("producto_stock", 0)).toString())
            editTiempo.setText((intent.getIntExtra("producto_tiempo", 1)).toString())
            editImagen.setText(intent.getStringExtra("producto_imagen") ?: "")
            editPersonalizacion.setText(intent.getStringExtra("producto_personalizacion") ?: "")
        }

        btnGuardar.setOnClickListener {
            val nombre = editNombre.text.toString().trim()
            val categoria = editCategoria.text.toString().trim()
            val precioStr = editPrecio.text.toString().trim()
            val stockStr = editStock.text.toString().trim()
            val tiempoStr = editTiempo.text.toString().trim()
            val imagenUrl = editImagen.text.toString().trim()
            val personalizacionStr = editPersonalizacion.text.toString().trim()

            if (nombre.isBlank()) {
                Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (precioStr.isBlank()) {
                Toast.makeText(this, "El precio es obligatorio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val precio = precioStr.toDoubleOrNull() ?: 0.0
            val stock = stockStr.toIntOrNull() ?: 0
            val tiempo = tiempoStr.toIntOrNull() ?: 1
            val opciones = if (personalizacionStr.isBlank()) emptyList() else personalizacionStr.split(",").map { it.trim() }

            val producto = Producto(
                id = productoId,
                nombre = nombre,
                categoria = categoria,
                precioBase = precio,
                stock = stock,
                tiempoProduccionDias = tiempo,
                opcionesPersonalizacion = opciones,
                activo = intent.getBooleanExtra("producto_activo", true),
                imagenUrl = imagenUrl
            )

            progressBar.visibility = View.VISIBLE
            btnGuardar.isEnabled = false

            lifecycleScope.launch {
                val result = repository.guardarProducto(producto)
                progressBar.visibility = View.GONE
                btnGuardar.isEnabled = true

                result.fold(
                    onSuccess = {
                        Toast.makeText(this@EditarProductoActivity, "Producto guardado exitosamente", Toast.LENGTH_SHORT).show()
                        finish()
                    },
                    onFailure = { e ->
                        Toast.makeText(this@EditarProductoActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                )
            }
        }
    }
}
