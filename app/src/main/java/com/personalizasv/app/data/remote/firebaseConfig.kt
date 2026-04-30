package com.personalizasv.app.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseConfig {
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    //Colecciones en Firebase Console
    const val COL_USUARIOS = "usuarios"
    const val COL_CLIENTES = "clientes"
    const val COL_PRODUCTOS = "productos"
    const val COL_PEDIDOS = "pedidos"
    const val COL_TRANSACCIONES = "transacciones"
    const val COL_RESUMEN_MENSUAL = "resumen_mensual"
}