package com.personalizasv.app.domain.repositories

import com.personalizasv.app.data.models.Transaccion

interface TransaccionRepository {

    suspend fun obtenerPorRango(inicioMs: Long, finMs: Long): List<Transaccion>
}