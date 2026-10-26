package com.example.myapplication.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.myapplication.model.Rol

data class ItemMenu(val screen: Screen, val icono: ImageVector)

private val inicio = ItemMenu(Screen.Dashboard, Icons.Filled.Home)
private val sensores = ItemMenu(Screen.Sensores, Icons.Filled.Info)
private val actuadores = ItemMenu(Screen.Actuadores, Icons.Filled.Build)
private val alertas = ItemMenu(Screen.Alertas, Icons.Filled.Notifications)
private val historial = ItemMenu(Screen.Historial, Icons.Filled.DateRange)
private val reportar = ItemMenu(Screen.ReportarIncidencia, Icons.Filled.Edit)
private val resumen = ItemMenu(Screen.ResumenEjecutivo, Icons.AutoMirrored.Filled.List)
private val usuarios = ItemMenu(Screen.Usuarios, Icons.Filled.Person)
private val configuracion = ItemMenu(Screen.Configuracion, Icons.Filled.Settings)
private val perfil = ItemMenu(Screen.Perfil, Icons.Filled.AccountCircle)

/** Opciones del menú lateral (NavigationDrawer) según el rol del usuario. */
fun opcionesDrawer(rol: Rol): List<ItemMenu> = when (rol) {
    Rol.SUPERVISOR -> listOf(inicio, sensores, actuadores, alertas, historial, reportar, perfil)
    Rol.TECNICO -> listOf(inicio, sensores, actuadores, alertas, historial, reportar, perfil)
    Rol.GERENCIA -> listOf(inicio, resumen, alertas, historial, perfil)
    Rol.ASTI -> listOf(inicio, sensores, actuadores, alertas, historial, usuarios, configuracion, perfil)
}

/** Opciones de la barra inferior (NavigationBar) según el rol del usuario. */
fun opcionesBarraInferior(rol: Rol): List<ItemMenu> = when (rol) {
    Rol.SUPERVISOR -> listOf(inicio, sensores, alertas, historial)
    Rol.TECNICO -> listOf(inicio, sensores, actuadores, alertas)
    Rol.GERENCIA -> listOf(inicio, resumen, alertas, historial)
    Rol.ASTI -> listOf(inicio, alertas, usuarios, configuracion)
}
