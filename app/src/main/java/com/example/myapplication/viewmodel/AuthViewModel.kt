package com.example.myapplication.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.AgroDbHelper
import com.example.myapplication.data.repository.UsuarioRepository
import com.example.myapplication.model.Rol
import com.example.myapplication.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val cargando: Boolean = false,
    val error: String? = null,
    val mensaje: String? = null,
    val registroExitoso: Boolean = false,
    val usuario: Usuario? = null,
    val usuarios: List<Usuario> = emptyList()
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UsuarioRepository(AgroDbHelper(application))

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(error = "Ingresa tu correo y contraseña") }
            return
        }
        _uiState.update { it.copy(cargando = true, error = null) }
        viewModelScope.launch {
            repository.login(email, password)
                .onSuccess { usuario -> _uiState.update { it.copy(cargando = false, usuario = usuario) } }
                .onFailure { e -> _uiState.update { it.copy(cargando = false, error = e.message) } }
        }
    }

    fun registrar(nombre: String, email: String, password: String, confirmacion: String, rol: Rol?) {
        val error = when {
            nombre.isBlank() -> "Ingresa tu nombre"
            !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> "Correo electrónico no válido"
            password.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            password != confirmacion -> "Las contraseñas no coinciden"
            rol == null -> "Selecciona un rol"
            else -> null
        }
        if (error != null) {
            _uiState.update { it.copy(error = error) }
            return
        }
        _uiState.update { it.copy(cargando = true, error = null) }
        viewModelScope.launch {
            repository.registrar(nombre, email, password, rol!!)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            registroExitoso = true,
                            mensaje = "Cuenta creada. Ya puedes iniciar sesión."
                        )
                    }
                }
                .onFailure { e -> _uiState.update { it.copy(cargando = false, error = e.message) } }
        }
    }

    fun cargarUsuarios() {
        viewModelScope.launch {
            val lista = repository.listarUsuarios()
            _uiState.update { it.copy(usuarios = lista) }
        }
    }

    fun logout() {
        _uiState.value = AuthUiState(mensaje = "Sesión cerrada")
    }

    fun registroAtendido() = _uiState.update { it.copy(registroExitoso = false) }

    fun limpiarError() = _uiState.update { it.copy(error = null) }

    fun limpiarMensaje() = _uiState.update { it.copy(mensaje = null) }
}
