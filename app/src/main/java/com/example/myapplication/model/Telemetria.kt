package com.example.myapplication.model

enum class EstadoSensor(val etiqueta: String) {
    OK("Normal"),
    ADVERTENCIA("Advertencia"),
    FALLA("Sin señal")
}

/** Lecturas de sensores de un cuartel del predio. */
data class LecturaCuartel(
    val id: String,
    val nombre: String,
    val cultivo: String,
    val humedadSuelo: Int,
    val temperatura: Double,
    val humedadRelativa: Int,
    val radiacionSolar: Int,
    val estado: EstadoSensor
)

data class Estanque(
    val nombre: String,
    val nivel: Int,
    val caudal: Double
)

data class Actuador(
    val id: String,
    val nombre: String,
    val tipo: String,
    val cuartel: String,
    val activo: Boolean
)

enum class Severidad(val etiqueta: String) {
    CRITICA("Crítica"),
    ALTA("Alta"),
    MEDIA("Media")
}

data class Alerta(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val cuartel: String,
    val severidad: Severidad,
    val fechaHora: String,
    val activa: Boolean
)

data class Evento(
    val fechaHora: String,
    val titulo: String,
    val detalle: String,
    val cuartel: String
)
