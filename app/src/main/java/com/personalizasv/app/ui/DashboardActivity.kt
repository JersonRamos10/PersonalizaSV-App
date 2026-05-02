package com.personalizasv.app.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.personalizasv.app.R
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.personalizasv.app.data.remote.FirebaseConfig

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        findViewById<MaterialCardView>(R.id.editRegistrar).setOnClickListener {
            startActivity(Intent(this, RegistrarClienteActivity::class.java))
        }

        findViewById<MaterialCardView>(R.id.editPedido).setOnClickListener {
            startActivity(Intent(this, RegistrarPedidoActivity::class.java))
        }

        findViewById<MaterialCardView>(R.id.editGestion).setOnClickListener {
            Toast.makeText(this, "Próximamente: Gestión de Productos", Toast.LENGTH_SHORT).show()
        }

        findViewById<MaterialCardView>(R.id.editSeguimiento).setOnClickListener {
            Toast.makeText(this, "Próximamente: Seguimiento", Toast.LENGTH_SHORT).show()
        }

        findViewById<MaterialCardView>(R.id.editIngresos).setOnClickListener {
            Toast.makeText(this, "Próximamente: Ingresos y Egresos", Toast.LENGTH_SHORT).show()
        }

        findViewById<MaterialCardView>(R.id.editVentas).setOnClickListener {
            Toast.makeText(this, "Próximamente: Ventas Mensuales", Toast.LENGTH_SHORT).show()
        }

        findViewById<MaterialCardView>(R.id.editHistorial).setOnClickListener {
            Toast.makeText(this, "Próximamente: Historial Clientes", Toast.LENGTH_SHORT).show()
        }

        findViewById<MaterialCardView>(R.id.editMetodo).setOnClickListener {
            Toast.makeText(this, "Próximamente: Métodos de Pago", Toast.LENGTH_SHORT).show()
        }

        findViewById<MaterialCardView>(R.id.editConfig).setOnClickListener {
            Toast.makeText(this, "Próximamente: Configuración", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            FirebaseConfig.auth.signOut()
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
