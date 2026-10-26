package com.example.myapplication.model

data class Usuario(
    val id: Long,
    val nombre: String,
    val email: String,
    val rol: Rol
)
