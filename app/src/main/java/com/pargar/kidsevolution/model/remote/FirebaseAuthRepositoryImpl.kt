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
    }

    override suspend fun signUp(
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
                    mData.child("Users").child(uid).setValue(map)
                }
            }
            isSuccessful
        } catch (e: Exception) {
            false
        }
    }

}