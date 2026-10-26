package com.example.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.model.Rol
import com.example.myapplication.viewmodel.AuthUiState

/** Contenedor centrado con ancho máximo: se adapta a teléfonos y tablets. */
@Composable
private fun ContenedorAuth(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.widthIn(max = 480.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) { content() }
    }
}

@Composable
fun LoginScreen(
    estado: AuthUiState,
    onLogin: (String, String) -> Unit,
    onIrARegistro: () -> Unit,
    onLimpiarError: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    ContenedorAuth {
        Spacer(Modifier.height(24.dp))
        Image(
            painter = painterResource(R.drawable.ic_agroalerta_logo),
            contentDescription = "Logo AgroAlerta",
            modifier = Modifier.size(96.dp)
        )
        Text("AgroAlerta", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
        Text("Monitoreo de telemetría y alertas del predio", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it; onLimpiarError() },
            label = { Text("Correo electrónico") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; onLimpiarError() },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        estado.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Button(
            onClick = { onLogin(email, password) },
            enabled = !estado.cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (estado.cargando) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
            else Text("Ingresar")
        }
        TextButton(onClick = onIrARegistro) { Text("¿No tienes cuenta? Regístrate") }

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Usuarios de prueba (clave 123456)", style = MaterialTheme.typography.labelLarge)
                Text("supervisor@frutis.cl · tecnico@frutis.cl", style = MaterialTheme.typography.bodySmall)
                Text("gerencia@frutis.cl · admin@asti.cl", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun RegistroScreen(
    estado: AuthUiState,
    onRegistrar: (String, String, String, String, Rol?) -> Unit,
    onRegistroExitoso: () -> Unit,
    onVolverALogin: () -> Unit,
    onLimpiarError: () -> Unit
) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmacion by rememberSaveable { mutableStateOf("") }
    var rol by rememberSaveable { mutableStateOf<Rol?>(null) }

    LaunchedEffect(estado.registroExitoso) {
        if (estado.registroExitoso) onRegistroExitoso()
    }

    ContenedorAuth {
        Text("Crear cuenta", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Text("Registra tus datos para acceder a AgroAlerta", style = MaterialTheme.typography.bodyMedium)

        OutlinedTextField(
            value = nombre, onValueChange = { nombre = it; onLimpiarError() },
            label = { Text("Nombre completo") }, singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = email, onValueChange = { email = it; onLimpiarError() },
            label = { Text("Correo electrónico") }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password, onValueChange = { password = it; onLimpiarError() },
            label = { Text("Contraseña (mín. 6 caracteres)") }, singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = confirmacion, onValueChange = { confirmacion = it; onLimpiarError() },
            label = { Text("Confirmar contraseña") }, singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Text("Rol", style = MaterialTheme.typography.titleSmall, modifier = Modifier.fillMaxWidth())
        Rol.entries.forEach { opcion ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(selected = rol == opcion, onClick = { rol = opcion; onLimpiarError() }),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = rol == opcion, onClick = { rol = opcion; onLimpiarError() })
                Column {
                    Text(opcion.etiqueta, style = MaterialTheme.typography.bodyLarge)
                    Text(opcion.descripcion, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        estado.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Button(
            onClick = { onRegistrar(nombre, email, password, confirmacion, rol) },
            enabled = !estado.cargando,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Registrarme") }
        TextButton(onClick = onVolverALogin) { Text("¿Ya tienes cuenta? Inicia sesión") }
    }
}
