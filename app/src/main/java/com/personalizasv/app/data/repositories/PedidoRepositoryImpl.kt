package com.personalizasv.app.data.repositories

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.personalizasv.app.data.models.Pedido
import com.personalizasv.app.data.remote.FirebaseConfig
import com.personalizasv.app.domain.repositories.PedidoRepository
import kotlinx.coroutines.tasks.await

class PedidoRepositoryImpl : PedidoRepository {

    override suspend fun obtenerPedidosPorEstado(estado: String): List<Pedido> {
        return try {
            val snapshot = FirebaseConfig.db.collection(FirebaseConfig.COL_PEDIDOS)
                .whereEqualTo("estado", estado)
                .get()
                .await()
            snapshot.toObjects(Pedido::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun crearPedido(pedido: Pedido): Result<String> {
        return try {
            val docRef = FirebaseConfig.db.collection(FirebaseConfig.COL_PEDIDOS).document()
            val pedidoConId = pedido.copy(id = docRef.id)
            docRef.set(pedidoConId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun actualizarEstado(idPedido: String, nuevoEstado: String): Result<Unit> {
        return try {
            FirebaseConfig.db.collection(FirebaseConfig.COL_PEDIDOS)
                .document(idPedido)
                .update("estado", nuevoEstado)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerTodosPedidos(): List<Pedido> {
        return try {
            val snapshot = FirebaseConfig.db.collection(FirebaseConfig.COL_PEDIDOS)
                .orderBy("fechaPedido", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Pedido::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun buscarPedidos(query: String): List<Pedido> {
        return try {
            val snapshot = FirebaseConfig.db.collection(FirebaseConfig.COL_PEDIDOS).get().await()
            val pedidos = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Pedido::class.java)?.copy(id = doc.id)
            }
            val q = query.lowercase().trim()
            pedidos.filter { p ->
                p.id.lowercase().contains(q) ||
                p.nombreCliente.lowercase().contains(q)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun obtenerPedidoPorId(id: String): Pedido? {
        return try {
            val doc = FirebaseConfig.db.collection(FirebaseConfig.COL_PEDIDOS).document(id).get().await()
            doc.toObject(Pedido::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }
}