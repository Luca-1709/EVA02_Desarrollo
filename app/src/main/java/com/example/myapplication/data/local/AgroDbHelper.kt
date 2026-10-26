package com.example.myapplication.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.myapplication.model.Rol
import com.example.myapplication.model.Usuario
import java.security.MessageDigest

/**
 * Base de datos local SQLite (almacenamiento offline) para el registro
 * y la autenticación de usuarios.
 */
class AgroDbHelper(context: Context) :
    SQLiteOpenHelper(context, DB_NOMBRE, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLA_USUARIOS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NOMBRE TEXT NOT NULL,
                $COL_EMAIL TEXT NOT NULL UNIQUE,
                $COL_PASSWORD TEXT NOT NULL,
                $COL_ROL TEXT NOT NULL,
                $COL_FECHA INTEGER NOT NULL
            )
            """.trimIndent()
        )
        // Usuarios de demostración, uno por cada rol (contraseña: 123456)
        insertar(db, "Sofía Supervisora", "supervisor@frutis.cl", "123456", Rol.SUPERVISOR)
        insertar(db, "Tomás Técnico", "tecnico@frutis.cl", "123456", Rol.TECNICO)
        insertar(db, "Gabriela Gerente", "gerencia@frutis.cl", "123456", Rol.GERENCIA)
        insertar(db, "Andrés ASTI", "admin@asti.cl", "123456", Rol.ASTI)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLA_USUARIOS")
        onCreate(db)
    }

    /** Retorna el id del nuevo usuario o -1 si el correo ya existe. */
    fun insertarUsuario(nombre: String, email: String, password: String, rol: Rol): Long =
        insertar(writableDatabase, nombre, email, password, rol)

    /** Retorna el usuario si el correo y la contraseña coinciden, o null. */
    fun autenticar(email: String, password: String): Usuario? {
        readableDatabase.query(
            TABLA_USUARIOS,
            null,
            "$COL_EMAIL = ? AND $COL_PASSWORD = ?",
            arrayOf(email.lowercase(), hash(password)),
            null, null, null
        ).use { cursor ->
            return if (cursor.moveToFirst()) {
                Usuario(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE)),
                    email = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)),
                    rol = Rol.desdeNombre(cursor.getString(cursor.getColumnIndexOrThrow(COL_ROL)))
                )
            } else null
        }
    }

    fun existeEmail(email: String): Boolean {
        readableDatabase.query(
            TABLA_USUARIOS, arrayOf(COL_ID), "$COL_EMAIL = ?",
            arrayOf(email.lowercase()), null, null, null
        ).use { return it.count > 0 }
    }

    fun listarUsuarios(): List<Usuario> {
        val lista = mutableListOf<Usuario>()
        readableDatabase.query(
            TABLA_USUARIOS, null, null, null, null, null, "$COL_NOMBRE ASC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(
                    Usuario(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)),
                        nombre = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE)),
                        email = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)),
                        rol = Rol.desdeNombre(cursor.getString(cursor.getColumnIndexOrThrow(COL_ROL)))
                    )
                )
            }
        }
        return lista
    }

    private fun insertar(db: SQLiteDatabase, nombre: String, email: String, password: String, rol: Rol): Long {
        val valores = ContentValues().apply {
            put(COL_NOMBRE, nombre)
            put(COL_EMAIL, email.lowercase())
            put(COL_PASSWORD, hash(password))
            put(COL_ROL, rol.name)
            put(COL_FECHA, System.currentTimeMillis())
        }
        return db.insert(TABLA_USUARIOS, null, valores)
    }

    /** La contraseña nunca se guarda en texto plano: se almacena su hash SHA-256. */
    private fun hash(texto: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(texto.toByteArray())
            .joinToString("") { "%02x".format(it) }

    companion object {
        private const val DB_NOMBRE = "agroalerta.db"
        private const val DB_VERSION = 1
        private const val TABLA_USUARIOS = "usuarios"
        private const val COL_ID = "id"
        private const val COL_NOMBRE = "nombre"
        private const val COL_EMAIL = "email"
        private const val COL_PASSWORD = "password_hash"
        private const val COL_ROL = "rol"
        private const val COL_FECHA = "fecha_registro"
    }
}
