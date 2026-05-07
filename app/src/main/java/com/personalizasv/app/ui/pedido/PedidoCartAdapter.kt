package com.personalizasv.app.ui.pedido

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.personalizasv.app.R
import com.personalizasv.app.data.models.Personalizacion

class PedidoCartAdapter(
    private val onCantidadChange: (Int, Int) -> Unit,
    private val onEliminar: (Int) -> Unit,
    private val onPersonalizacionChange: (Int, Personalizacion) -> Unit
) : ListAdapter<CartItemPedido, PedidoCartAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pedido_cart, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtNombre: TextView = itemView.findViewById(R.id.txtNombreProducto)
        private val txtCantidad: TextView = itemView.findViewById(R.id.txtCantidad)
        private val txtSubtotal: TextView = itemView.findViewById(R.id.txtSubtotal)
        private val btnMenos: MaterialButton = itemView.findViewById(R.id.btnMenos)
        private val btnMas: MaterialButton = itemView.findViewById(R.id.btnMas)
        private val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminarItem)
        private val editTalla: EditText = itemView.findViewById(R.id.editTalla)
        private val editColor: EditText = itemView.findViewById(R.id.editColor)
        private val editTextoPersonalizado: EditText = itemView.findViewById(R.id.editTextoPersonalizado)

        private var currentPosition: Int = 0

        init {
            editTalla.addTextChangedListener(object : android.text.TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: android.text.Editable?) {
                    savePersonalizacion()
                }
            })
            editColor.addTextChangedListener(object : android.text.TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: android.text.Editable?) {
                    savePersonalizacion()
                }
            })
            editTextoPersonalizado.addTextChangedListener(object : android.text.TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: android.text.Editable?) {
                    savePersonalizacion()
                }
            })
        }

        private fun savePersonalizacion() {
            val personalizacion = Personalizacion(
                talla = editTalla.text.toString(),
                color = editColor.text.toString(),
                textoPersonalizado = editTextoPersonalizado.text.toString()
            )
            onPersonalizacionChange(currentPosition, personalizacion)
        }

        fun bind(item: CartItemPedido, position: Int) {
            currentPosition = position

            txtNombre.text = item.producto.nombre
            txtCantidad.text = item.cantidad.toString()
            txtSubtotal.text = "$${"%.2f".format(item.subtotal)}"

            if (editTalla.hasFocus().not()) {
                val currentText = editTalla.text.toString()
                if (currentText != item.personalizacion.talla) {
                    editTalla.setText(item.personalizacion.talla)
                }
            }
            if (editColor.hasFocus().not()) {
                val currentText = editColor.text.toString()
                if (currentText != item.personalizacion.color) {
                    editColor.setText(item.personalizacion.color)
                }
            }
            if (editTextoPersonalizado.hasFocus().not()) {
                val currentText = editTextoPersonalizado.text.toString()
                if (currentText != item.personalizacion.textoPersonalizado) {
                    editTextoPersonalizado.setText(item.personalizacion.textoPersonalizado)
                }
            }

            btnMenos.setOnClickListener {
                onCantidadChange(currentPosition, item.cantidad - 1)
            }

            btnMas.setOnClickListener {
                onCantidadChange(currentPosition, item.cantidad + 1)
            }

            btnEliminar.setOnClickListener {
                onEliminar(currentPosition)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CartItemPedido>() {
        override fun areItemsTheSame(oldItem: CartItemPedido, newItem: CartItemPedido): Boolean {
            return oldItem.producto.id == newItem.producto.id
        }

        override fun areContentsTheSame(oldItem: CartItemPedido, newItem: CartItemPedido): Boolean {
            return oldItem.producto.id == newItem.producto.id &&
                   oldItem.cantidad == newItem.cantidad
        }
    }
}
