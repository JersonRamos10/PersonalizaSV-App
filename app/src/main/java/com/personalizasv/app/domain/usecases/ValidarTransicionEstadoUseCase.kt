package com.personalizasv.app.domain.usecases

class ValidarTransicionEstadoUseCase {

    private val transicionesValidas = mapOf(
        "pendiente" to listOf("diseno_aprobado", "cancelado"),
        "diseno_aprobado" to listOf("en_produccion", "cancelado"),
        "en_produccion" to listOf("listo_entrega"),
        "listo_entrega" to listOf("entregado"),
        "entregado" to emptyList(),
        "cancelado" to emptyList()
    )

    operator fun invoke(estadoActual: String, nuevoEstado: String): Boolean {
        return transicionesValidas[estadoActual]?.contains(nuevoEstado) == true
    }
}