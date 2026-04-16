package com.personalizasv.app.data.repositories

import com.personalizasv.app.data.models.Producto
import com.personalizasv.app.data.remote.FirebaseConfig
import kotlinx.coroutines.tasks.await

class ProductoRepositoryImpl {
    suspend fun obtenerProductosActivos(): List<Producto> {
        return try {
            val snapshot = FirebaseConfig.db.collection(FirebaseConfig.COL_PRODUCTOS)
                .whereEqualTo("activo", true)
                .get()
                .await()
            snapshot.toObjects(Producto::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}