package com.example.myapplication.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.model.Alerta
import com.example.myapplication.model.EstadoSensor
import com.example.myapplication.model.Rol
import com.example.myapplication.model.Usuario
import com.example.myapplication.navigation.Screen
import com.example.myapplication.ui.components.Etiqueta
import com.example.myapplication.ui.components.TarjetaIndicador
import com.example.myapplication.ui.components.TituloSeccion
import com.example.myapplication.ui.components.colorEstado
import com.example.myapplication.ui.components.colorSeveridad
import com.example.myapplication.ui.theme.EstadoAdvertencia
import com.example.myapplication.ui.theme.EstadoCritico
import com.example.myapplication.ui.theme.EstadoOk
import com.example.myapplication.ui.utils.TamanoVentana
import com.example.myapplication.ui.utils.obtenerTamanoVentana
import com.example.myapplication.viewmodel.MonitoreoViewModel

@Composable
fun DashboardScreen(
    usuario: Usuario,
    viewModel: MonitoreoViewModel,
    onNavegar: (String) -> Unit
) {
    val tamano = obtenerTamanoVentana()
    // Indicadores: 2 columnas en teléfono, 4 en pantallas más anchas
    val columnas = if (tamano == TamanoVentana.COMPACTO) 2 else 4
    val nivelEstanque = viewModel.estanques.first().nivel

    LazyVerticalGrid(
        columns = GridCells.Fixed(columnas),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        anchoCompleto {
            Column {
                Text("Hola, ${usuario.nombre}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("${usuario.rol.etiqueta} · ${viewModel.predio}", style = MaterialTheme.typography.bodyMedium)
                Text(
                    "Última sincronización: ${viewModel.ultimaSincronizacion} (caché local)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        item {
            TarjetaIndicador(
                "Alertas activas", viewModel.alertasActivas.size.toString(), "Requieren atención",
                EstadoCritico, Modifier.clickable { onNavegar(Screen.Alertas.route) }
            )
        }
        item {
            TarjetaIndicador(
                "Temp. mínima", "%.1f °C".format(viewModel.temperaturaMinima), "Riesgo de helada < 2 °C",
                if (viewModel.temperaturaMinima < 2) EstadoCritico else EstadoOk
            )
        }
        item {
            TarjetaIndicador(
                "Estanque", "$nivelEstanque %", "Nivel principal",
                if (nivelEstanque < 25) EstadoAdvertencia else EstadoOk
            )
        }
        item {
            TarjetaIndicador(
                "Actuadores", "${viewModel.actuadoresActivos}/${viewModel.actuadores.size}", "Encendidos",
                MaterialTheme.colorScheme.primary
            )
        }

        when (usuario.rol) {
            Rol.SUPERVISOR -> {
                anchoCompleto { TituloSeccion("Alertas críticas") }
                items(viewModel.alertasActivas.take(3), span = { GridItemSpan(maxLineSpan) }) { alerta ->
                    FilaAlerta(alerta) { onNavegar(Screen.DetalleAlerta.crearRuta(alerta.id)) }
                }
                anchoCompleto {
                    Button(onClick = { onNavegar(Screen.ReportarIncidencia.route) }, Modifier.fillMaxWidth()) {
                        Text("Reportar incidencia")
                    }
                }
            }
            Rol.TECNICO -> {
                anchoCompleto { TituloSeccion("Equipos que requieren revisión") }
                items(
                    viewModel.cuarteles.filter { it.estado != EstadoSensor.OK },
                    span = { GridItemSpan(maxLineSpan) }
                ) { cuartel ->
                    Card(Modifier.fillMaxWidth().clickable { onNavegar(Screen.Sensores.route) }) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("${cuartel.nombre} · ${cuartel.cultivo}", style = MaterialTheme.typography.titleSmall)
                                Text("Estación de sensores: ${cuartel.estado.etiqueta}", style = MaterialTheme.typography.bodySmall)
                            }
                            Etiqueta(cuartel.estado.etiqueta, colorEstado(cuartel.estado))
                        }
                    }
                }
                anchoCompleto {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = { onNavegar(Screen.Actuadores.route) }, Modifier.weight(1f)) { Text("Ver actuadores") }
                        Button(onClick = { onNavegar(Screen.ReportarIncidencia.route) }, Modifier.weight(1f)) { Text("Reportar mantención") }
                    }
                }
            }
            Rol.GERENCIA -> {
                anchoCompleto { TituloSeccion("Visión ejecutiva") }
                anchoCompleto {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Estado general del predio: CON ALERTAS", fontWeight = FontWeight.Bold, color = EstadoAdvertencia)
                            Text("${viewModel.cuarteles.size} cuarteles monitoreados · ${viewModel.alertas.count { !it.activa }} alertas resueltas en 24 h")
                            Text("Tiempo medio de reacción estimado: 12 min", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                anchoCompleto {
                    Button(onClick = { onNavegar(Screen.ResumenEjecutivo.route) }, Modifier.fillMaxWidth()) {
                        Text("Ver resumen ejecutivo")
                    }
                }
            }
            Rol.ASTI -> {
                anchoCompleto { TituloSeccion("Administración de la solución") }
                anchoCompleto {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Conexión con plataforma de supervisión: simulada (offline)")
                            Text("Estaciones con falla: ${viewModel.cuarteles.count { it.estado == EstadoSensor.FALLA }}")
                        }
                    }
                }
                anchoCompleto {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = { onNavegar(Screen.Usuarios.route) }, Modifier.weight(1f)) { Text("Usuarios") }
                        Button(onClick = { onNavegar(Screen.Configuracion.route) }, Modifier.weight(1f)) { Text("Configuración") }
                    }
                }
            }
        }
    }
}

private fun LazyGridScope.anchoCompleto(content: @Composable () -> Unit) {
    item(span = { GridItemSpan(maxLineSpan) }) { content() }
}

@Composable
fun FilaAlerta(alerta: Alerta, onClick: () -> Unit) {
    val color = colorSeveridad(alerta.severidad)
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (alerta.activa) color.copy(alpha = 0.10f) else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(alerta.titulo, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text("${alerta.cuartel} · ${alerta.fechaHora}", style = MaterialTheme.typography.bodySmall)
            }
            Etiqueta(if (alerta.activa) alerta.severidad.etiqueta else "Resuelta", if (alerta.activa) color else EstadoOk)
        }
    }
}
