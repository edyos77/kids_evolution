package com.pargar.kidsevolution.model.remote

import android.util.Log
import com.pargar.kidsevolution.model.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepositoryImpl @Inject constructor(private val firebaseAuth: FirebaseAuth ):
    AuthRepository {

/*
    override suspend fun login(email: String, password: String): Boolean {
        return try {
            var isSuccessful: Boolean = false

            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { isSuccessful = true }
                .addOnFailureListener { isSuccessful = false }
                .await()
            isSuccessful
        } catch (e: Exception) {
            Log.d("test", e.toString())
            false
        }
    }*/

    override suspend fun login(email: String, password: String): Boolean {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            result.user != null // o simplemente true
        } catch (e: Exception) {
            Log.d("LoginError", "Login falló: ${e.localizedMessage}")
            false
        }
    }

    override suspend fun signUp(
        email: String, password: String, nomPadre: String, nomHijo: String,
        apellidos: String, cedula: String, telefono: String, edad: String
    ): Boolean {
        return try {
            // Registrar el usuario con Firebase Authentication
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: return false

            // Mapa de datos a guardar
            val map = mapOf(
                "Correo" to email,
                "NombrePadre" to nomPadre,
                "NombreHijo" to nomHijo,
                "Apellidos" to apellidos,
                "Cedula" to cedula,
                "Telefono" to telefono,
                "Edad" to edad
            )

            // Guardar en Firebase Realtime Database
            val mData: DatabaseReference = FirebaseDatabase.getInstance().getReference()
            mData.child("usuarios").child(userId).setValue(map).await()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

/*    override suspend fun signUp(
        email: String, password: String, nomPadre: String, nomHijo: String,
        apellidos: String, cedula: String, telefono: String, edad: String
    ): Boolean {
        return try {
            var isSuccessful: Boolean = false
            val mData: DatabaseReference = FirebaseDatabase.getInstance().getReference()

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    isSuccessful = task.isSuccessful
                }
                .await()

            if (isSuccessful) {
                val map = mutableMapOf<String, String>()
                map["Correo"] = email
                map["NombrePadre"] = nomPadre
                map["NombreHijo"] = nomHijo
                map["Apellidos"] = apellidos
                map["Cedula"] = cedula
                map["Telefono"] = telefono
                map["Edad"] = edad

                val userId = firebaseAuth.currentUser?.uid
                userId?.let { uid ->
                    mData.child("usuarios").child(uid).setValue(map)
                }
            }
            isSuccessful
        } catch (e: Exception) {
            false
        }
    }*/

}