package com.personalizasv.app.data.models

data class Cliente(
    val id: String = "",
    val nombreCompleto: String = "",
    val telefono: String = "",
    val correo: String = "",
    val direccion: String = "",
    val fechaRegistro: Long = 0L, // Timestamp en milisegundos
    val totalPedidos: Int = 0,
    val ultimoPedido: Long = 0L, // Timestamp en milisegundos
    val notas: String = ""
)