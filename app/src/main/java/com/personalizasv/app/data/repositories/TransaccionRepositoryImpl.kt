package com.personalizasv.app.data.repositories

import com.personalizasv.app.data.models.Transaccion
import com.personalizasv.app.data.remote.FirebaseConfig
import com.personalizasv.app.domain.repositories.TransaccionRepository
import kotlinx.coroutines.tasks.await

class TransaccionRepositoryImpl : TransaccionRepository {

    override suspend fun obtenerPorRango(inicioMs: Long, finMs: Long): List<Transaccion> {
        return try {
            val snapshot = FirebaseConfig.db.collection(FirebaseConfig.COL_TRANSACCIONES)
                .whereGreaterThanOrEqualTo("fecha", inicioMs)
                .whereLessThan("fecha", finMs)
                .get()
                .await()
            snapshot.toObjects(Transaccion::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}