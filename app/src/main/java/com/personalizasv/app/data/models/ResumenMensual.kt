package com.personalizasv.app.data.models

data class ResumenMensual(
    val id: String = "",
    val mes: String = "",         // Ej: "Abril"
    val anio: Int = 0,            // Ej: 2026
    val totalIngresos: Double = 0.0, // Suma de todos los ingresos del mes
    val totalEgresos: Double = 0.0,  // Suma de todos los gastos del mes
    val gananciaNeta: Double = 0.0,  // Ingresos - Egresos
    val cantidadPedidos: Int = 0,    // Total de pedidos creados en el mes
    val pedidosEntregados: Int = 0,  // Pedidos finalizados (Éxito)
    val productoMasVendido: String = "" // Insight estratégico
)