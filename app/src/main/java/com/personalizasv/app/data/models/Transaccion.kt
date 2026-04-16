package com.personalizasv.app.data.models

data class Transaccion(
    val id: String = "",
    val tipo: String = "", // "ingreso" o "egreso"
    val monto: Double = 0.0,
    val categoria: String = "", // "venta_pedido", "materiales", "envios", "comision_revendedor", "plataformas", "servicios", "otros"
    val descripcion: String = "",
    val fecha: Long = 0L, // Timestamp en milisegundos
    val idPedidoRelacionado: String = "",
    val metodoPago: String = "", // "efectivo", "transferencia", "nequi"
    val registradoPor: String = "", // UID del usuario
    val comprobanteUrl: String = ""
)