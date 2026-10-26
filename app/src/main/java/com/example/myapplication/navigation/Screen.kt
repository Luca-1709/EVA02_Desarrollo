package com.example.myapplication.navigation

/** Rutas de la aplicación. */
sealed class Screen(val route: String, val titulo: String) {
    data object Login : Screen("login", "Iniciar sesión")
    data object Registro : Screen("registro", "Crear cuenta")
    data object Dashboard : Screen("dashboard", "Inicio")
    data object Sensores : Screen("sensores", "Sensores")
    data object Actuadores : Screen("actuadores", "Actuadores")
    data object Alertas : Screen("alertas", "Alertas")
    data object DetalleAlerta : Screen("alerta/{alertaId}", "Detalle de alerta") {
        const val ARG_ID = "alertaId"
        fun crearRuta(id: Int) = "alerta/$id"
    }
    data object Historial : Screen("historial", "Historial")
    data object ReportarIncidencia : Screen("reportar", "Reportar incidencia")
    data object ResumenEjecutivo : Screen("resumen", "Resumen ejecutivo")
    data object Usuarios : Screen("usuarios", "Gestión de usuarios")
    data object Configuracion : Screen("configuracion", "Configuración remota")
    data object Perfil : Screen("perfil", "Mi perfil")

    companion object {
        private val todas by lazy {
            listOf(
                Login, Registro, Dashboard, Sensores, Actuadores, Alertas, DetalleAlerta,
                Historial, ReportarIncidencia, ResumenEjecutivo, Usuarios, Configuracion, Perfil
            )
        }

        fun desdeRuta(route: String?): Screen? = todas.firstOrNull { it.route == route }

        /** Pantallas secundarias: muestran flecha "volver" en lugar del menú. */
        val secundarias by lazy { listOf(DetalleAlerta.route, ReportarIncidencia.route) }
    }
}
