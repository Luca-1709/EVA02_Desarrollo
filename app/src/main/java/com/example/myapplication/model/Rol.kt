package com.example.myapplication.model

/** Perfiles de usuario definidos en el caso AgroAlerta. */
enum class Rol(val etiqueta: String, val descripcion: String) {
    SUPERVISOR("Supervisor agrícola", "Monitorea el predio y reacciona ante alertas"),
    TECNICO("Técnico de mantenimiento", "Revisa sensores y actuadores en terreno"),
    GERENCIA("Gerencia", "Visibilidad ejecutiva del estado del predio"),
    ASTI("Equipo técnico ASTI", "Soporte y configuración remota de la solución");

    companion object {
        fun desdeNombre(nombre: String): Rol = entries.firstOrNull { it.name == nombre } ?: SUPERVISOR
    }
}
