package com.personalizasv.app.data.repositories

import com.personalizasv.app.data.models.Cliente
import com.personalizasv.app.data.remote.FirebaseConfig
import kotlinx.coroutines.tasks.await

class ClienteRepositoryImpl {
    suspend fun buscarPorTelefono(telefono: String): Cliente? {
        return try {
            val snapshot = FirebaseConfig.db.collection(FirebaseConfig.COL_CLIENTES)
                .whereEqualTo("telefono", telefono)
                .limit(1)
                .get()
                .await()
            snapshot.toObjects(Cliente::class.java).firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun registrarCliente(cliente: Cliente): Result<String> {
        return try {
            val docRef = FirebaseConfig.db.collection(FirebaseConfig.COL_CLIENTES).document()
            val clienteConId = cliente.copy(id = docRef.id)
            docRef.set(clienteConId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}