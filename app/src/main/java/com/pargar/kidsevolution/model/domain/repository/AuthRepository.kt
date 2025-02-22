package com.pargar.kidsevolution.model.domain.repository

interface AuthRepository {

    suspend fun login(email: String, password:String): Boolean

    suspend fun signUp(email: String, password: String, nomPadre: String, nomHijo: String, apellidos: String, cedula: String, telefono: String, edad: String): Boolean




}