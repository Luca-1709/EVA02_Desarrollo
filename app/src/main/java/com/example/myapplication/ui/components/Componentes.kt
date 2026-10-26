package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.model.EstadoSensor
import com.example.myapplication.model.Severidad
import com.example.myapplication.ui.theme.EstadoAdvertencia
import com.example.myapplication.ui.theme.EstadoCritico
import com.example.myapplication.ui.theme.EstadoOk

/** Mensaje mostrado al enviar formularios que aún no están conectados a la base de datos remota. */
@Composable
fun FuturaActualizacionDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Filled.Info, contentDescription = null) },
        title = { Text("Para una futura actualización") },
        text = {
            Text("Esta funcionalidad aún no está conectada a la base de datos. Estará disponible en una próxima versión de AgroAlerta.")
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Entendido") } }
    )
}

@Composable
fun TituloSeccion(texto: String, modifier: Modifier = Modifier) {
    Text(
        text = texto,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
fun TarjetaIndicador(titulo: String, valor: String, detalle: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f))
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(titulo, style = MaterialTheme.typography.labelLarge)
            Text(valor, style = MaterialTheme.typography.headlineMedium, color = color, fontWeight = FontWeight.Bold)
            Text(detalle, style = MaterialTheme.typography.bodySmall)
        }
    }
}

fun colorSeveridad(severidad: Severidad): Color = when (severidad) {
    Severidad.CRITICA -> EstadoCritico
    Severidad.ALTA -> EstadoAdvertencia
    Severidad.MEDIA -> Color(0xFF1E88E5)
}

fun colorEstado(estado: EstadoSensor): Color = when (estado) {
    EstadoSensor.OK -> EstadoOk
    EstadoSensor.ADVERTENCIA -> EstadoAdvertencia
    EstadoSensor.FALLA -> EstadoCritico
}

@Composable
fun Etiqueta(texto: String, color: Color) {
    Surface(color = color, shape = MaterialTheme.shapes.small) {
        Text(
            text = texto,
            color = Color.White,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}
