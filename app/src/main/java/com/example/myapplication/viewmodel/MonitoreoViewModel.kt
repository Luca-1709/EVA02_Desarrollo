package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.MockData
import com.example.myapplication.model.Alerta

/**
 * Expone la telemetría del predio. Por ahora usa la caché simulada (MockData);
 * en una futura versión se obtendrá desde la API REST mediante Retrofit.
 */
class MonitoreoViewModel : ViewModel() {
    val predio = MockData.PREDIO
    val ultimaSincronizacion = MockData.ULTIMA_SINCRONIZACION
    val cuarteles = MockData.cuarteles
    val estanques = MockData.estanques
    val actuadores = MockData.actuadores
    val alertas = MockData.alertas
    val historial = MockData.historial

    val alertasActivas: List<Alerta> get() = alertas.filter { it.activa }
    val temperaturaMinima: Double get() = cuarteles.minOf { it.temperatura }
    val actuadoresActivos: Int get() = actuadores.count { it.activo }

    fun alerta(id: Int): Alerta? = MockData.alertaPorId(id)
}
