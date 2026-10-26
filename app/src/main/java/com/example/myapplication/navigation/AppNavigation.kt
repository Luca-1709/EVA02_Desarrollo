package com.example.myapplication.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.ui.components.AgroBottomBar
import com.example.myapplication.ui.components.AgroDrawer
import com.example.myapplication.ui.components.AgroNavigationRail
import com.example.myapplication.ui.components.AgroTopBar
import com.example.myapplication.ui.screens.ActuadoresScreen
import com.example.myapplication.ui.screens.AlertasScreen
import com.example.myapplication.ui.screens.ConfiguracionScreen
import com.example.myapplication.ui.screens.DashboardScreen
import com.example.myapplication.ui.screens.DetalleAlertaScreen
import com.example.myapplication.ui.screens.HistorialScreen
import com.example.myapplication.ui.screens.LoginScreen
import com.example.myapplication.ui.screens.PerfilScreen
import com.example.myapplication.ui.screens.RegistroScreen
import com.example.myapplication.ui.screens.ReportarIncidenciaScreen
import com.example.myapplication.ui.screens.ResumenEjecutivoScreen
import com.example.myapplication.ui.screens.SensoresScreen
import com.example.myapplication.ui.screens.UsuariosScreen
import com.example.myapplication.ui.utils.TamanoVentana
import com.example.myapplication.ui.utils.obtenerTamanoVentana
import com.example.myapplication.viewmodel.AuthViewModel
import com.example.myapplication.viewmodel.MonitoreoViewModel
import kotlinx.coroutines.launch

/** Navega a una sección principal sin apilar pantallas repetidas. */
private fun NavHostController.navegarSeccion(route: String) {
    navigate(route) {
        popUpTo(Screen.Dashboard.route) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun AgroAlertaApp(
    authViewModel: AuthViewModel = viewModel(),
    monitoreoViewModel: MonitoreoViewModel = viewModel()
) {
    val navController = rememberNavController()
    val authState by authViewModel.uiState.collectAsState()
    val usuario = authState.usuario
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val tamano = obtenerTamanoVentana()

    val mostrarBarras = usuario != null && rutaActual != null &&
        rutaActual != Screen.Login.route && rutaActual != Screen.Registro.route
    val esSecundaria = rutaActual in Screen.secundarias
    val itemsDrawer = usuario?.let { opcionesDrawer(it.rol) } ?: emptyList()
    val itemsBarra = usuario?.let { opcionesBarraInferior(it.rol) } ?: emptyList()

    // Al iniciar sesión se pasa al dashboard y se elimina el login de la pila
    LaunchedEffect(usuario) {
        if (usuario != null && navController.currentDestination?.route == Screen.Login.route) {
            navController.navigate(Screen.Dashboard.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }
    LaunchedEffect(authState.mensaje) {
        authState.mensaje?.let {
            snackbarHostState.showSnackbar(it)
            authViewModel.limpiarMensaje()
        }
    }

    val seleccionarSeccion: (String) -> Unit = { route ->
        scope.launch { drawerState.close() }
        navController.navegarSeccion(route)
    }
    val cerrarSesion: () -> Unit = {
        scope.launch { drawerState.close() }
        authViewModel.logout()
        navController.navigate(Screen.Login.route) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = mostrarBarras,
        drawerContent = {
            AgroDrawer(usuario, itemsDrawer, rutaActual, seleccionarSeccion, cerrarSesion)
        }
    ) {
        Scaffold(
            topBar = {
                if (mostrarBarras) {
                    AgroTopBar(
                        titulo = Screen.desdeRuta(rutaActual)?.titulo ?: "AgroAlerta",
                        mostrarVolver = esSecundaria,
                        onMenu = { scope.launch { drawerState.open() } },
                        onVolver = { navController.popBackStack() },
                        onAlertas = { navController.navegarSeccion(Screen.Alertas.route) }
                    )
                }
            },
            bottomBar = {
                if (mostrarBarras && tamano == TamanoVentana.COMPACTO) {
                    AgroBottomBar(itemsBarra, rutaActual) { navController.navegarSeccion(it) }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Row(Modifier.fillMaxSize().padding(innerPadding)) {
                if (mostrarBarras && tamano != TamanoVentana.COMPACTO) {
                    AgroNavigationRail(itemsBarra, rutaActual) { navController.navegarSeccion(it) }
                }
                NavHost(
                    navController = navController,
                    startDestination = Screen.Login.route,
                    modifier = Modifier.weight(1f)
                ) {
                    composable(Screen.Login.route) {
                        LoginScreen(
                            estado = authState,
                            onLogin = authViewModel::login,
                            onIrARegistro = {
                                authViewModel.limpiarError()
                                navController.navigate(Screen.Registro.route)
                            },
                            onLimpiarError = authViewModel::limpiarError
                        )
                    }
                    composable(Screen.Registro.route) {
                        RegistroScreen(
                            estado = authState,
                            onRegistrar = authViewModel::registrar,
                            onRegistroExitoso = {
                                authViewModel.registroAtendido()
                                navController.popBackStack()
                            },
                            onVolverALogin = {
                                authViewModel.limpiarError()
                                navController.popBackStack()
                            },
                            onLimpiarError = authViewModel::limpiarError
                        )
                    }
                    composable(Screen.Dashboard.route) {
                        val actual = usuario ?: return@composable
                        DashboardScreen(actual, monitoreoViewModel) { route -> navController.navigate(route) }
                    }
                    composable(Screen.Sensores.route) { SensoresScreen(monitoreoViewModel) }
                    composable(Screen.Actuadores.route) { ActuadoresScreen(monitoreoViewModel) }
                    composable(Screen.Alertas.route) {
                        AlertasScreen(monitoreoViewModel) { id ->
                            navController.navigate(Screen.DetalleAlerta.crearRuta(id))
                        }
                    }
                    composable(
                        Screen.DetalleAlerta.route,
                        arguments = listOf(navArgument(Screen.DetalleAlerta.ARG_ID) { type = NavType.IntType })
                    ) { entry ->
                        val id = entry.arguments?.getInt(Screen.DetalleAlerta.ARG_ID) ?: 0
                        DetalleAlertaScreen(id, monitoreoViewModel) { navController.popBackStack() }
                    }
                    composable(Screen.Historial.route) { HistorialScreen(monitoreoViewModel) }
                    composable(Screen.ReportarIncidencia.route) {
                        ReportarIncidenciaScreen(monitoreoViewModel) { navController.popBackStack() }
                    }
                    composable(Screen.ResumenEjecutivo.route) { ResumenEjecutivoScreen(monitoreoViewModel) }
                    composable(Screen.Usuarios.route) {
                        UsuariosScreen(authState.usuarios, authViewModel::cargarUsuarios)
                    }
                    composable(Screen.Configuracion.route) { ConfiguracionScreen() }
                    composable(Screen.Perfil.route) {
                        val actual = usuario ?: return@composable
                        PerfilScreen(actual, cerrarSesion)
                    }
                }
            }
        }
    }
}
