package com.example.myapplication.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

/** Clases de tamaño de ventana según los puntos de corte de Material 3. */
enum class TamanoVentana { COMPACTO, MEDIANO, EXPANDIDO }

@Composable
fun obtenerTamanoVentana(): TamanoVentana {
    val ancho = LocalConfiguration.current.screenWidthDp
    return when {
        ancho < 600 -> TamanoVentana.COMPACTO
        ancho < 840 -> TamanoVentana.MEDIANO
        else -> TamanoVentana.EXPANDIDO
    }
}

/** Número de columnas sugerido para grillas según el tamaño de ventana. */
fun TamanoVentana.columnas(): Int = when (this) {
    TamanoVentana.COMPACTO -> 1
    TamanoVentana.MEDIANO -> 2
    TamanoVentana.EXPANDIDO -> 3
}
