package com.personalizasv.app.data.models

data class Pedido(
val id: String = "",
val idCliente: String = "",
val idRevendedor: String = "",
val estado: String = "pendiente", // pendiente, diseno_aprobado, en_produccion, listo_entrega, entregado, cancelado
val productos: List<ItemPedido> = emptyList(),
val metodoPago: String = "",
val estadoPago: String = "pendiente",
val totalPedido: Double = 0.0,
val fechaPedido: Long = 0L,  // Timestamp en milisegundos
val fechaEntregaEstimada: Long = 0L,
val direccionEntrega: String = "",
val notasAdicionales: String = ""
)

data class ItemPedido(
    val cantidad: Int = 0,
    val idProducto: String = "",
    val nombreProducto: String = "",
    val precioUnitario: Double = 0.0,
    val personalizacion: Personalizacion = Personalizacion()
)

data class Personalizacion(
    val talla: String = "",
    val color: String = "",
    val textoPersonalizado: String = ""
)




