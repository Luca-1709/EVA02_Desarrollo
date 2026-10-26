package com.example.myapplication.data

import com.example.myapplication.model.Actuador
import com.example.myapplication.model.Alerta
import com.example.myapplication.model.EstadoSensor
import com.example.myapplication.model.Estanque
import com.example.myapplication.model.Evento
import com.example.myapplication.model.LecturaCuartel
import com.example.myapplication.model.Severidad

/**
 * Datos ficticios de telemetría (cliente anonimizado "Agrícola FRUTIS").
 * Simulan la última lectura conocida mientras no exista la conexión con la API REST.
 */
object MockData {

    const val PREDIO = "Agrícola FRUTIS · Predio Buin"
    const val ULTIMA_SINCRONIZACION = "Hoy 06:45"

    val cuarteles = listOf(
        LecturaCuartel("C1", "Cuartel 1", "Cerezos", 32, 1.4, 88, 120, EstadoSensor.ADVERTENCIA),
        LecturaCuartel("C2", "Cuartel 2", "Nogales", 41, 3.8, 81, 135, EstadoSensor.OK),
        LecturaCuartel("C3", "Cuartel 3", "Ciruelos", 18, 4.2, 76, 140, EstadoSensor.ADVERTENCIA),
        LecturaCuartel("C4", "Cuartel 4", "Uva de mesa", 45, 5.1, 72, 150, EstadoSensor.FALLA)
    )

    val estanques = listOf(
        Estanque("Estanque principal", 22, 0.0),
        Estanque("Estanque secundario", 68, 145.5)
    )

    val actuadores = listOf(
        Actuador("EV-01", "Electroválvula 01", "Electroválvula", "Cuartel 1", true),
        Actuador("EV-02", "Electroválvula 02", "Electroválvula", "Cuartel 2", false),
        Actuador("EV-03", "Electroválvula 03", "Electroválvula", "Cuartel 3", true),
        Actuador("BI-01", "Bomba de impulsión 01", "Bomba de impulsión", "Sala de bombas", true),
        Actuador("BI-02", "Bomba de impulsión 02", "Bomba de impulsión", "Sala de bombas", false),
        Actuador("AH-01", "Anti-heladas Norte", "Sistema anti-heladas", "Cuartel 1", true),
        Actuador("AH-02", "Anti-heladas Sur", "Sistema anti-heladas", "Cuartel 4", false)
    )

    val alertas = listOf(
        Alerta(1, "Riesgo de helada", "Temperatura de 1,4 °C y descendiendo. Se activó el sistema anti-heladas Norte.", "Cuartel 1", Severidad.CRITICA, "Hoy 05:12", true),
        Alerta(2, "Nivel crítico de agua", "El estanque principal está bajo el 25 % de su capacidad.", "Estanque principal", Severidad.CRITICA, "Hoy 04:58", true),
        Alerta(3, "Posible fuga u obstrucción", "Caudal anómalo detectado en la línea de riego de la electroválvula 03.", "Cuartel 3", Severidad.ALTA, "Hoy 03:30", true),
        Alerta(4, "Sensor sin señal", "La estación meteorológica no reporta lecturas hace 40 minutos.", "Cuartel 4", Severidad.MEDIA, "Hoy 02:15", true),
        Alerta(5, "Humedad de suelo baja", "Humedad de suelo bajo el umbral configurado (20 %).", "Cuartel 3", Severidad.MEDIA, "Ayer 18:40", false)
    )

    val historial = listOf(
        Evento("Hoy 05:15", "Anti-heladas activado", "Activación automática por temperatura < 2 °C", "Cuartel 1"),
        Evento("Hoy 05:12", "Alerta de helada", "Severidad crítica notificada a supervisores", "Cuartel 1"),
        Evento("Hoy 04:58", "Nivel de estanque crítico", "Nivel 22 % en estanque principal", "Estanque principal"),
        Evento("Hoy 03:30", "Caudal anómalo", "Posible fuga en línea de riego EV-03", "Cuartel 3"),
        Evento("Hoy 02:15", "Pérdida de señal", "Estación meteorológica sin lecturas", "Cuartel 4"),
        Evento("Ayer 22:00", "Riego nocturno finalizado", "Electroválvulas EV-01 y EV-03 cerradas", "Cuarteles 1 y 3"),
        Evento("Ayer 18:40", "Humedad de suelo baja", "Alerta resuelta por inicio de riego", "Cuartel 3"),
        Evento("Ayer 09:10", "Mantención bomba BI-02", "Revisión preventiva registrada por técnico", "Sala de bombas")
    )

    fun alertaPorId(id: Int): Alerta? = alertas.firstOrNull { it.id == id }
}
