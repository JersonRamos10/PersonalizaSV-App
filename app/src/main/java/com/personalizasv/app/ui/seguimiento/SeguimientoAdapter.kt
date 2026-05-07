package com.personalizasv.app.ui.seguimiento

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.personalizasv.app.R
import com.personalizasv.app.data.models.Pedido
import java.text.SimpleDateFormat
import java.util.Locale

class SeguimientoAdapter(
    private val onClick: (Pedido) -> Unit
) : RecyclerView.Adapter<SeguimientoAdapter.ViewHolder>() {

    private var lista: List<Pedido> = emptyList()

    fun submitList(nuevaLista: List<Pedido>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_seguimiento_pedido, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lista[position])
    }

    override fun getItemCount(): Int = lista.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtId: TextView = itemView.findViewById(R.id.txtPedidoId)
        private val txtCliente: TextView = itemView.findViewById(R.id.txtPedidoCliente)
        private val txtEstado: TextView = itemView.findViewById(R.id.txtPedidoEstado)
        private val txtFecha: TextView = itemView.findViewById(R.id.txtPedidoFecha)
        private val txtTotal: TextView = itemView.findViewById(R.id.txtPedidoTotal)

        fun bind(pedido: Pedido) {
            txtId.text = "Pedido #${pedido.id.take(8)}"
            txtCliente.text = "Cliente: ${pedido.nombreCliente}"
            txtFecha.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(pedido.fechaPedido)
            txtTotal.text = "$${"%.2f".format(pedido.totalPedido)}"

            txtEstado.text = when (pedido.estado) {
                "pendiente" -> "Pendiente"
                "diseno_aprobado" -> "Diseño Aprobado"
                "en_produccion" -> "En Producción"
                "listo_entrega" -> "Listo para Entrega"
                "entregado" -> "Entregado"
                "cancelado" -> "Cancelado"
                else -> pedido.estado.replace("_", " ").replaceFirstChar { it.uppercase() }
            }

            val colorRes = when (pedido.estado) {
                "pendiente" -> R.color.warning
                "diseno_aprobado" -> R.color.info
                "en_produccion" -> R.color.primary
                "listo_entrega" -> R.color.success
                "entregado" -> R.color.success
                "cancelado" -> R.color.error
                else -> R.color.text_secondary
            }
            txtEstado.setTextColor(itemView.context.getColor(colorRes))

            itemView.setOnClickListener { onClick(pedido) }
        }
    }
}
