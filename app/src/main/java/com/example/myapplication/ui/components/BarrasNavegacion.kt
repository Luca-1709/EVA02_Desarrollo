package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.myapplication.model.Usuario
import com.example.myapplication.navigation.ItemMenu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgroTopBar(
    titulo: String,
    mostrarVolver: Boolean,
    onMenu: () -> Unit,
    onVolver: () -> Unit,
    onAlertas: () -> Unit
) {
    TopAppBar(
        title = { Text(titulo, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        navigationIcon = {
            if (mostrarVolver) {
                IconButton(onClick = onVolver) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
            } else {
                IconButton(onClick = onMenu) {
                    Icon(Icons.Filled.Menu, contentDescription = "Abrir menú")
                }
            }
        },
        actions = {
            IconButton(onClick = onAlertas) {
                Icon(Icons.Filled.Notifications, contentDescription = "Alertas")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
fun AgroBottomBar(items: List<ItemMenu>, rutaActual: String?, onSeleccionar: (String) -> Unit) {
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = rutaActual == item.screen.route,
                onClick = { onSeleccionar(item.screen.route) },
                icon = { Icon(item.icono, contentDescription = item.screen.titulo) },
                label = { Text(item.screen.titulo, maxLines = 1, overflow = TextOverflow.Ellipsis) }
            )
        }
    }
}

/** En pantallas medianas o expandidas la barra inferior se reemplaza por un NavigationRail. */
@Composable
fun AgroNavigationRail(items: List<ItemMenu>, rutaActual: String?, onSeleccionar: (String) -> Unit) {
    NavigationRail {
        Spacer(Modifier.height(8.dp))
        items.forEach { item ->
            NavigationRailItem(
                selected = rutaActual == item.screen.route,
                onClick = { onSeleccionar(item.screen.route) },
                icon = { Icon(item.icono, contentDescription = item.screen.titulo) },
                label = { Text(item.screen.titulo, maxLines = 1) }
            )
        }
    }
}

@Composable
fun AgroDrawer(
    usuario: Usuario?,
    items: List<ItemMenu>,
    rutaActual: String?,
    onSeleccionar: (String) -> Unit,
    onCerrarSesion: () -> Unit
) {
    ModalDrawerSheet {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            Column(Modifier.padding(24.dp)) {
                Text("AgroAlerta", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                if (usuario != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(usuario.nombre, style = MaterialTheme.typography.titleMedium)
                    Text(usuario.rol.etiqueta, style = MaterialTheme.typography.bodyMedium)
                    Text(usuario.email, style = MaterialTheme.typography.bodySmall)
                }
            }
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))
            items.forEach { item ->
                NavigationDrawerItem(
                    label = { Text(item.screen.titulo) },
                    icon = { Icon(item.icono, contentDescription = null) },
                    selected = rutaActual == item.screen.route,
                    onClick = { onSeleccionar(item.screen.route) },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            NavigationDrawerItem(
                label = { Text("Cerrar sesión") },
                icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null) },
                selected = false,
                onClick = onCerrarSesion,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}
