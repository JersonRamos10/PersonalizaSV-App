package com.personalizasv.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.personalizasv.app.R
import android.widget.ImageButton

class SeguimientoPedidoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seguimiento_pedido)
        findViewById<ImageButton>(R.id.btnBack4).setOnClickListener {
            finish()
        }
    }
}