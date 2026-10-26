package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.model.EstadoSensor
import com.example.myapplication.model.LecturaCuartel
import com.example.myapplication.ui.components.Etiqueta
import com.example.myapplication.ui.components.FuturaActualizacionDialog
import com.example.myapplication.ui.components.TarjetaIndicador
import com.example.myapplication.ui.components.TituloSeccion
import com.example.myapplication.ui.components.colorEstado
import com.example.myapplication.ui.theme.EstadoAdvertencia
import com.example.myapplication.ui.theme.EstadoOk
import com.example.myapplication.ui.utils.columnas
import com.example.myapplication.ui.utils.obtenerTamanoVentana
import com.example.myapplication.viewmodel.MonitoreoViewModel

@Composable
fun SensoresScreen(viewModel: MonitoreoViewModel) {
    val columnas = obtenerTamanoVentana().columnas()
    LazyVerticalGrid(
        columns = GridCells.Fixed(columnas),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                "Últimas lecturas conocidas · ${viewModel.ultimaSincronizacion}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
        items(viewModel.cuarteles) { TarjetaCuartel(it) }
        item(span = { GridItemSpan(maxLineSpan) }) { TituloSeccion("Estanques") }
        items(viewModel.estanques) { estanque ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(estanque.nombre, style = MaterialTheme.typography.titleSmall)
                    LinearProgressIndicator(
                        progress = { estanque.nivel / 100f },
                        color = if (estanque.nivel < 25) EstadoAdvertencia else EstadoOk,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Nivel ${estanque.nivel} % · Caudal ${estanque.caudal} L/min", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun TarjetaCuartel(cuartel: LecturaCuartel) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(cuartel.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(cuartel.cultivo, style = MaterialTheme.typography.bodySmall)
                }
                Etiqueta(cuartel.estado.etiqueta, colorEstado(cuartel.estado))
            }
            HorizontalDivider(Modifier.padding(vertical = 4.dp))
            Lectura("Temperatura", "%.1f °C".format(cuartel.temperatura))
            Lectura("Humedad de suelo", "${cuartel.humedadSuelo} %")
            Lectura("Humedad relativa", "${cuartel.humedadRelativa} %")
            Lectura("Radiación solar", "${cuartel.radiacionSolar} W/m²")
        }
    }
}

@Composable
private fun Lectura(nombre: String, valor: String) {
    Row(Modifier.fillMaxWidth()) {
        Text(nombre, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Text(valor, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ActuadoresScreen(viewModel: MonitoreoViewModel) {
    var mostrarDialogo by remember { mutableStateOf(false) }
    val tipos = viewModel.actuadores.map { it.tipo }.distinct()

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        tipos.forEach { tipo ->
            item { TituloSeccion(tipo) }
            items(viewModel.actuadores.filter { it.tipo == tipo }) { actuador ->
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = if (actuador.activo) EstadoOk else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(12.dp)
                        ) {}
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(actuador.nombre, style = MaterialTheme.typography.titleSmall)
                            Text(
                                "${actuador.id} · ${actuador.cuartel} · ${if (actuador.activo) "Encendido" else "Apagado"}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        // El control remoto de actuadores requiere la API REST: se informa futura actualización
                        Switch(checked = actuador.activo, onCheckedChange = { mostrarDialogo = true })
                    }
                }
            }
        }
    }
    if (mostrarDialogo) FuturaActualizacionDialog { mostrarDialogo = false }
}

@Composable
fun AlertasScreen(viewModel: MonitoreoViewModel, onVerDetalle: (Int) -> Unit) {
    var soloActivas by remember { mutableStateOf(true) }
    val lista = if (soloActivas) viewModel.alertasActivas else viewModel.alertas

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = soloActivas, onClick = { soloActivas = true }, label = { Text("Activas") })
                FilterChip(selected = !soloActivas, onClick = { soloActivas = false }, label = { Text("Todas") })
            }
        }
        items(lista) { alerta -> FilaAlerta(alerta) { onVerDetalle(alerta.id) } }
    }
}

@Composable
fun HistorialScreen(viewModel: MonitoreoViewModel) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                "Historial reciente de eventos y lecturas",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        items(viewModel.historial) { evento ->
            Row(Modifier.padding(vertical = 8.dp)) {
                Text(
                    evento.fechaHora,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.width(88.dp)
                )
                Column(Modifier.weight(1f)) {
                    Text(evento.titulo, style = MaterialTheme.typography.titleSmall)
                    Text(evento.detalle, style = MaterialTheme.typography.bodySmall)
                    Text(evento.cuartel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                }
            }
            HorizontalDivider()
        }
    }
}

@Composable
fun ResumenEjecutivoScreen(viewModel: MonitoreoViewModel) {
    val columnas = obtenerTamanoVentana().columnas()
    LazyVerticalGrid(
        columns = GridCells.Fixed(columnas),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(viewModel.predio, style = MaterialTheme.typography.titleMedium)
        }
        item {
            TarjetaIndicador(
                "Alertas últimas 24 h", viewModel.alertas.size.toString(),
                "${viewModel.alertasActivas.size} activas · ${viewModel.alertas.size - viewModel.alertasActivas.size} resueltas",
                MaterialTheme.colorScheme.primary
            )
        }
        item {
            TarjetaIndicador(
                "Cuarteles operativos",
                "${viewModel.cuarteles.count { it.estado != EstadoSensor.FALLA }}/${viewModel.cuarteles.size}",
                "Con telemetría disponible", EstadoOk
            )
        }
        item {
            TarjetaIndicador(
                "Disponibilidad de agua", "${viewModel.estanques.sumOf { it.nivel } / viewModel.estanques.size} %",
                "Promedio estanques", EstadoAdvertencia
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }) { TituloSeccion("Estado por cuartel") }
        items(viewModel.cuarteles) { cuartel ->
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("${cuartel.nombre} · ${cuartel.cultivo}", Modifier.weight(1f))
                    Etiqueta(cuartel.estado.etiqueta, colorEstado(cuartel.estado))
                }
            }
        }
    }
}
