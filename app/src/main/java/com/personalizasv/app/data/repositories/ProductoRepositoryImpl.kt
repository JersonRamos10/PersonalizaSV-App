package com.personalizasv.app.data.repositories

import com.personalizasv.app.data.models.Producto
import com.personalizasv.app.data.remote.FirebaseConfig
import kotlinx.coroutines.tasks.await

class ProductoRepositoryImpl {
    private val collection = FirebaseConfig.db.collection(FirebaseConfig.COL_PRODUCTOS)

    suspend fun obtenerProductosActivos(): List<Producto> {
        return try {
            val snapshot = collection
                .whereEqualTo("activo", true)
                .get()
                .await()
            snapshot.toObjects(Producto::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun obtenerTodosProductos(): List<Producto> {
        return try {
            val snapshot = collection.get().await()
            snapshot.toObjects(Producto::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun guardarProducto(producto: Producto): Result<String> {
        return try {
            val docRef = if (producto.id.isBlank()) {
                collection.document()
            } else {
                collection.document(producto.id)
            }
            val productoConId = producto.copy(id = docRef.id)
            docRef.set(productoConId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cambiarEstadoProducto(id: String, activo: Boolean): Result<Unit> {
        return try {
            collection.document(id).update("activo", activo).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}