package com.personalizasv.app.data.models

data class Producto(
    val id: String = "",
    val nombre: String = "",
    val categoria: String = "",
    val precioBase: Double = 0.0,
    val stock: Int = 0,
    val tiempoProduccionDias: Int = 1,
    val opcionesPersonalizacion: List<String> = emptyList(),
    val activo: Boolean = true,
    val imagenUrl: String = ""

)
