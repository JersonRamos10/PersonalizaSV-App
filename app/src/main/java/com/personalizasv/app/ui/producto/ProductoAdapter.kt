package com.personalizasv.app.ui.producto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.personalizasv.app.R
import com.personalizasv.app.data.models.Producto

class ProductoAdapter(
    private val onEditar: (Producto) -> Unit,
    private val onActivar: (String) -> Unit,
    private val onDesactivar: (String) -> Unit
) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    private var lista: List<Producto> = emptyList()

    fun submitList(nuevaLista: List<Producto>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        holder.bind(lista[position])
    }

    override fun getItemCount(): Int = lista.size

    inner class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtNombre: TextView = itemView.findViewById(R.id.txtNombreProducto)
        private val txtCategoria: TextView = itemView.findViewById(R.id.txtCategoria)
        private val txtPrecio: TextView = itemView.findViewById(R.id.txtPrecio)
        private val txtStock: TextView = itemView.findViewById(R.id.txtStock)
        private val txtEstado: TextView = itemView.findViewById(R.id.txtEstado)
        private val btnMenu: ImageButton = itemView.findViewById(R.id.btnMenuProducto)

        fun bind(producto: Producto) {
            txtNombre.text = producto.nombre
            txtCategoria.text = producto.categoria.ifBlank { "Sin categoría" }
            txtPrecio.text = "\$%.2f".format(producto.precioBase)
            txtStock.text = "Stock: ${producto.stock}"

            if (producto.activo) {
                txtEstado.text = "Activo"
                txtEstado.setTextColor(itemView.context.getColor(R.color.primary))
            } else {
                txtEstado.text = "Inactivo"
                txtEstado.setTextColor(itemView.context.getColor(R.color.error))
            }

            btnMenu.setOnClickListener {
                val popup = PopupMenu(itemView.context, btnMenu)
                popup.menuInflater.inflate(R.menu.menu_producto, popup.menu)

                val itemActivar = popup.menu.findItem(R.id.action_activar)
                val itemDesactivar = popup.menu.findItem(R.id.action_desactivar)

                if (producto.activo) {
                    itemActivar.isVisible = false
                } else {
                    itemDesactivar.isVisible = false
                }

                popup.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.action_editar -> {
                            onEditar(producto)
                            true
                        }
                        R.id.action_desactivar -> {
                            onDesactivar(producto.id)
                            true
                        }
                        R.id.action_activar -> {
                            onActivar(producto.id)
                            true
                        }
                        else -> false
                    }
                }
                popup.show()
            }
        }
    }
}
