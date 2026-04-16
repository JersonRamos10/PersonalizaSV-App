package com.personalizasv.app.domain.repositories

import com.personalizasv.app.data.models.Pedido

interface PedidoRepository {
    suspend fun obtenerPedidosPorEstado(estado: String): List<Pedido>
    suspend fun crearPedido(pedido: Pedido): Result<String>
    suspend fun actualizarEstado(idPedido: String, nuevoEstado: String): Result<Unit>
}