package com.example.myapplication.data.repository

import com.example.myapplication.data.local.AgroDbHelper
import com.example.myapplication.model.Rol
import com.example.myapplication.model.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UsuarioRepository(private val db: AgroDbHelper) {

    suspend fun registrar(nombre: String, email: String, password: String, rol: Rol): Result<Long> =
        withContext(Dispatchers.IO) {
            if (db.existeEmail(email)) {
                Result.failure(IllegalStateException("El correo ya está registrado"))
            } else {
                val id = db.insertarUsuario(nombre.trim(), email.trim(), password, rol)
                if (id == -1L) Result.failure(IllegalStateException("No se pudo registrar el usuario"))
                else Result.success(id)
            }
        }

    suspend fun login(email: String, password: String): Result<Usuario> =
        withContext(Dispatchers.IO) {
            db.autenticar(email.trim(), password)
                ?.let { Result.success(it) }
                ?: Result.failure(IllegalArgumentException("Correo o contraseña incorrectos"))
        }

    suspend fun listarUsuarios(): List<Usuario> =
        withContext(Dispatchers.IO) { db.listarUsuarios() }
}
