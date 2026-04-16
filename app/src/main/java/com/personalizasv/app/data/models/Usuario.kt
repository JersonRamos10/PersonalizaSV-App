package com.personalizasv.app.data.models

data class Usuario(
    val uid: String = "",
    val nombreCompleto: String = "",
    val correo: String = "",
    val rol: String = "", // "admin" o "revendedor"
    val telefono: String = "",
    val fechaCreacion: Long = 0L, // Timestamp en milisegundos
    val activo: Boolean = true

)
