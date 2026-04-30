package com.personalizasv.app.ui.pedido

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.personalizasv.app.R
import com.personalizasv.app.data.models.Personalizacion

class PedidoCartAdapter(
    private val onCantidadChange: (Int, Int) -> Unit,
    private val onEliminar: (Int) -> Unit,
    private val onPersonalizacionChange: (Int, Personalizacion) -> Unit
) : RecyclerView.Adapter<PedidoCartAdapter.ViewHolder>() {

    private val items = mutableListOf<CartItemPedido>()

    fun submitList(newItems: List<CartItemPedido>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pedido_cart, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size

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
        private var textWatcher: android.text.TextWatcher? = null

        fun bind(item: CartItemPedido, position: Int) {
            currentPosition = position

            txtNombre.text = item.producto.nombre
            txtCantidad.text = item.cantidad.toString()
            txtSubtotal.text = "$${"%.2f".format(item.subtotal)}"

            editTalla.setText(item.personalizacion.talla)
            editColor.setText(item.personalizacion.color)
            editTextoPersonalizado.setText(item.personalizacion.textoPersonalizado)

            editTalla.removeTextChangedListener(textWatcher)
            editColor.removeTextChangedListener(textWatcher)
            editTextoPersonalizado.removeTextChangedListener(textWatcher)

            textWatcher = object : android.text.TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: android.text.Editable?) {
                    val personalizacion = Personalizacion(
                        talla = editTalla.text.toString(),
                        color = editColor.text.toString(),
                        textoPersonalizado = editTextoPersonalizado.text.toString()
                    )
                    onPersonalizacionChange(currentPosition, personalizacion)
                }
            }

            editTalla.addTextChangedListener(textWatcher)
            editColor.addTextChangedListener(textWatcher)
            editTextoPersonalizado.addTextChangedListener(textWatcher)

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
}
