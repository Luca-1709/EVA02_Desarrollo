package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myapplication.model.Severidad
import com.example.myapplication.model.Usuario
import com.example.myapplication.ui.components.Etiqueta
import com.example.myapplication.ui.components.FuturaActualizacionDialog
import com.example.myapplication.ui.components.TituloSeccion
import com.example.myapplication.ui.components.colorSeveridad
import com.example.myapplication.viewmodel.MonitoreoViewModel

/** Columna desplazable con ancho máximo para formularios (se centra en tablets). */
@Composable
private fun ContenedorFormulario(content: @Composable () -> Unit) {
    Box(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            Modifier.widthIn(max = 640.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) { content() }
    }
}

@Composable
private fun SelectorChips(opciones: List<String>, seleccion: String?, onSeleccion: (String) -> Unit) {
    // Se muestran en filas de 2 para que quepan en pantallas pequeñas
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        opciones.chunked(2).forEach { fila ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                fila.forEach { opcion ->
                    FilterChip(selected = seleccion == opcion, onClick = { onSeleccion(opcion) }, label = { Text(opcion) })
                }
            }
        }
    }
}

@Composable
fun DetalleAlertaScreen(alertaId: Int, viewModel: MonitoreoViewModel, onVolver: () -> Unit) {
    val alerta = viewModel.alerta(alertaId)
    if (alerta == null) {
        Text("La alerta no existe", modifier = Modifier.padding(16.dp))
        return
    }
    var accion by rememberSaveable { mutableStateOf("") }
    var observaciones by rememberSaveable { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var mostrarDialogo by remember { mutableStateOf(false) }

    ContenedorFormulario {
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(alerta.titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Etiqueta(alerta.severidad.etiqueta, colorSeveridad(alerta.severidad))
                }
                Text("Origen: ${alerta.cuartel}", style = MaterialTheme.typography.bodyMedium)
                Text("Detectada: ${alerta.fechaHora}", style = MaterialTheme.typography.bodyMedium)
                Text(if (alerta.activa) "Estado: Activa" else "Estado: Resuelta", style = MaterialTheme.typography.bodyMedium)
                Text(alerta.descripcion, style = MaterialTheme.typography.bodyLarge)
            }
        }

        TituloSeccion("Registrar acción")
        SelectorChips(
            listOf("Revisión en terreno", "Activación manual", "Escalar a ASTI", "Falsa alarma"),
            accion
        ) { accion = it; error = null }
        OutlinedTextField(
            value = observaciones,
            onValueChange = { observaciones = it; error = null },
            label = { Text("Observaciones") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(
            onClick = {
                if (accion.isBlank()) error = "Selecciona la acción realizada"
                else mostrarDialogo = true
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Registrar acción") }
        OutlinedButton(onClick = onVolver, modifier = Modifier.fillMaxWidth()) { Text("Volver") }
    }
    if (mostrarDialogo) FuturaActualizacionDialog { mostrarDialogo = false }
}

@Composable
fun ReportarIncidenciaScreen(viewModel: MonitoreoViewModel, onVolver: () -> Unit) {
    var cuartel by rememberSaveable { mutableStateOf("") }
    var tipo by rememberSaveable { mutableStateOf("") }
    var severidad by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var mostrarDialogo by remember { mutableStateOf(false) }

    ContenedorFormulario {
        Text("Registra una revisión o incidencia detectada en terreno.", style = MaterialTheme.typography.bodyMedium)

        TituloSeccion("Cuartel o estación")
        SelectorChips(viewModel.cuarteles.map { it.nombre } + "Sala de bombas", cuartel) { cuartel = it; error = null }

        TituloSeccion("Tipo de incidencia")
        SelectorChips(listOf("Helada", "Fuga / obstrucción", "Nivel de agua", "Falla de sensor", "Falla de actuador", "Otro"), tipo) {
            tipo = it; error = null
        }

        TituloSeccion("Severidad")
        SelectorChips(Severidad.entries.map { it.etiqueta }, severidad) { severidad = it; error = null }

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it; error = null },
            label = { Text("Descripción") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(
            onClick = {
                error = when {
                    cuartel.isBlank() -> "Selecciona el cuartel"
                    tipo.isBlank() -> "Selecciona el tipo de incidencia"
                    severidad.isBlank() -> "Selecciona la severidad"
                    descripcion.isBlank() -> "Describe la incidencia"
                    else -> null
                }
                if (error == null) mostrarDialogo = true
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Enviar reporte") }
        OutlinedButton(onClick = onVolver, modifier = Modifier.fillMaxWidth()) { Text("Cancelar") }
    }
    if (mostrarDialogo) FuturaActualizacionDialog { mostrarDialogo = false }
}

@Composable
fun ConfiguracionScreen() {
    var tempHelada by rememberSaveable { mutableStateOf("2.0") }
    var nivelMinimo by rememberSaveable { mutableStateOf("25") }
    var humedadMinima by rememberSaveable { mutableStateOf("20") }
    var frecuencia by rememberSaveable { mutableStateOf("5") }
    var notificaciones by rememberSaveable { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var mostrarDialogo by remember { mutableStateOf(false) }

    ContenedorFormulario {
        Text("Umbrales de alerta y parámetros de sincronización del predio.", style = MaterialTheme.typography.bodyMedium)
        CampoNumero("Temperatura de helada (°C)", tempHelada) { tempHelada = it; error = null }
        CampoNumero("Nivel mínimo de estanque (%)", nivelMinimo) { nivelMinimo = it; error = null }
        CampoNumero("Humedad de suelo mínima (%)", humedadMinima) { humedadMinima = it; error = null }
        CampoNumero("Frecuencia de sincronización (min)", frecuencia) { frecuencia = it; error = null }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Notificaciones push de alertas críticas", modifier = Modifier.weight(1f))
            Switch(checked = notificaciones, onCheckedChange = { notificaciones = it })
        }
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(
            onClick = {
                val valores = listOf(tempHelada, nivelMinimo, humedadMinima, frecuencia)
                error = if (valores.any { it.replace(',', '.').toDoubleOrNull() == null }) "Todos los campos deben ser numéricos" else null
                if (error == null) mostrarDialogo = true
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Guardar configuración") }
    }
    if (mostrarDialogo) FuturaActualizacionDialog { mostrarDialogo = false }
}

@Composable
private fun CampoNumero(etiqueta: String, valor: String, onCambio: (String) -> Unit) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun PerfilScreen(usuario: Usuario, onCerrarSesion: () -> Unit) {
    var nombre by rememberSaveable { mutableStateOf(usuario.nombre) }
    var telefono by rememberSaveable { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var mostrarDialogo by remember { mutableStateOf(false) }

    ContenedorFormulario {
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(usuario.nombre, style = MaterialTheme.typography.titleLarge)
                Text(usuario.email, style = MaterialTheme.typography.bodyMedium)
                Text(usuario.rol.etiqueta, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                Text(usuario.rol.descripcion, style = MaterialTheme.typography.bodySmall)
            }
        }
        TituloSeccion("Editar datos")
        OutlinedTextField(
            value = nombre, onValueChange = { nombre = it; error = null },
            label = { Text("Nombre") }, singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = telefono, onValueChange = { telefono = it; error = null },
            label = { Text("Teléfono de contacto") }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(
            onClick = {
                error = if (nombre.isBlank()) "El nombre es obligatorio" else null
                if (error == null) mostrarDialogo = true
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Guardar cambios") }
        OutlinedButton(onClick = onCerrarSesion, modifier = Modifier.fillMaxWidth()) { Text("Cerrar sesión") }
    }
    if (mostrarDialogo) FuturaActualizacionDialog { mostrarDialogo = false }
}
