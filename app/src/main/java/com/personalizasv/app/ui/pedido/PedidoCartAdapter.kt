package com.personalizasv.app.ui.pedido

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
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
        private val layoutTallaColor: LinearLayout = itemView.findViewById(R.id.layoutTallaColor)
        private val inputTalla: TextInputLayout = itemView.findViewById(R.id.inputTalla)
        private val inputColor: TextInputLayout = itemView.findViewById(R.id.inputColor)
        private val layoutTextoPersonalizado: TextInputLayout = itemView.findViewById(R.id.layoutTextoPersonalizado)

        private var currentPosition: Int = 0

        init {
            val onFocusLost = View.OnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    val personalizacion = Personalizacion(
                        talla = editTalla.text.toString(),
                        color = editColor.text.toString(),
                        textoPersonalizado = editTextoPersonalizado.text.toString()
                    )
                    onPersonalizacionChange(currentPosition, personalizacion)
                }
            }
            editTalla.onFocusChangeListener = onFocusLost
            editColor.onFocusChangeListener = onFocusLost
            editTextoPersonalizado.onFocusChangeListener = onFocusLost
        }

        fun bind(item: CartItemPedido, position: Int) {
            currentPosition = position

            txtNombre.text = item.producto.nombre
            txtCantidad.text = item.cantidad.toString()
            txtSubtotal.text = "$${"%.2f".format(item.subtotal)}"

            val opciones = item.producto.opcionesPersonalizacion.map { it.lowercase().trim() }
            val muestraTalla = opciones.contains("talla")
            val muestraColor = opciones.contains("color")
            val muestraTexto = opciones.contains("texto") || opciones.contains("texto personalizado")

            layoutTallaColor.visibility = if (muestraTalla || muestraColor) View.VISIBLE else View.GONE
            inputTalla.visibility = if (muestraTalla) View.VISIBLE else View.GONE
            inputColor.visibility = if (muestraColor) View.VISIBLE else View.GONE
            layoutTextoPersonalizado.visibility = if (muestraTexto) View.VISIBLE else View.GONE

            if (!editTalla.hasFocus() && editTalla.text.toString() != item.personalizacion.talla) {
                editTalla.setText(item.personalizacion.talla)
            }
            if (!editColor.hasFocus() && editColor.text.toString() != item.personalizacion.color) {
                editColor.setText(item.personalizacion.color)
            }
            if (!editTextoPersonalizado.hasFocus() && editTextoPersonalizado.text.toString() != item.personalizacion.textoPersonalizado) {
                editTextoPersonalizado.setText(item.personalizacion.textoPersonalizado)
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
            return oldItem == newItem
        }
    }
}
