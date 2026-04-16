package com.personalizasv.app.domain.usecases

import com.personalizasv.app.domain.repositories.TransaccionRepository

import java.util.Calendar

class CalcularGananciaNetaUseCase(
    private val repository: TransaccionRepository
) {
    /**
     * Calcula la ganancia neta de un mes específico
     * @param mes Número del mes (1=Enero, 12=Diciembre)
     * @param anio Año completo (ej: 2026)
     */
   suspend operator fun invoke(mes: Int, anio: Int): Result<Double> {
        return try {
            val calendar = Calendar.getInstance()

            //  Inicio del mes: Día 1, 00:00:00.000
            calendar.set(anio, mes - 1, 1, 0, 0, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val inicioMs = calendar.timeInMillis

            // Fin del mes: Ultimo día, 23:59:59.999
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            val finMs = calendar.timeInMillis

            //  Calculo financiero
            val transacciones = repository.obtenerPorRango(inicioMs, finMs)
            val ingresos = transacciones.filter { it.tipo == "ingreso" }.sumOf { it.monto }
            val egresos = transacciones.filter { it.tipo == "egreso" }.sumOf { it.monto }

            Result.success(ingresos - egresos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}