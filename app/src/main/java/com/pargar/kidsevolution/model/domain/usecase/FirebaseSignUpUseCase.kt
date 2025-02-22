package com.pargar.kidsevolution.model.domain.usecase

import android.util.Patterns
import com.pargar.kidsevolution.model.domain.repository.AuthRepository
import com.pargar.kidsevolution.model.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FirebaseSignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(
        email: String, password: String, nomPadre: String, nomHijo: String,
        apellidos: String, cedula: String, telefono: String, edad: String
    ): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        try {
            if (isValidName(nomPadre) && isValidName(nomHijo)) {
                if (isValidApellidos(apellidos)) {
                    if (isValidCedula(cedula)) {
                        if (isValidTelefono(telefono)) {
                            if (isValidEdad(edad)) {
                                if (isValidEmail(email)) {
                                    if (isValidPassword(password)) {
                                        val isSignUpSuccessfully = authRepository.signUp(email, password, nomPadre, nomHijo, apellidos, cedula, telefono, edad)
                                        if (isSignUpSuccessfully) {
                                            emit(Resource.Success(true))
                                        } else {
                                            emit(Resource.Error("Fallo en el registro. Por favor inténtelo más tarde."))
                                        }
                                    } else {
                                        emit(Resource.Error("Formato de contraseña inválido. La contraseña debe contener al menos 8 caracteres, 1 letra y un número."))
                                    }
                                } else {
                                    emit(Resource.Error("Correo inválido"))
                                }
                            } else {
                                emit(Resource.Error("La edad debe ser un número válido."))
                            }
                        } else {
                            emit(Resource.Error("El teléfono debe ser un número válido."))
                        }
                    } else {
                        emit(Resource.Error("La cédula debe ser un número válido."))
                    }
                } else {
                    emit(Resource.Error("Los apellidos no pueden estar vacíos."))
                }
            } else {
                emit(Resource.Error("Los nombres no pueden estar vacíos."))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error al registrarse: ${e.message}"))
        }
    }

    private fun isValidEmail(email: String): Boolean {
        // Aquí puedes implementar la lógica para verificar si el formato del correo es válido
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        // Aquí puedes implementar la lógica para verificar si el formato de la contraseña es válido
        val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$".toRegex()
        return passwordPattern.matches(password)
    }

    private fun isValidName(name: String): Boolean {
        // Aquí puedes implementar la lógica para verificar si el nombre es válido
        return name.isNotBlank()
    }

    private fun isValidApellidos(apellidos: String): Boolean {
        // Aquí puedes implementar la lógica para verificar si los apellidos son válidos
        return apellidos.isNotBlank()
    }

    private fun isValidCedula(cedula: String): Boolean {
        // Aquí puedes implementar la lógica para verificar si la cédula es válida
        return cedula.isNotBlank() && cedula.matches("\\d+".toRegex())
    }

    private fun isValidTelefono(telefono: String): Boolean {
        // Aquí puedes implementar la lógica para verificar si el teléfono es válido
        return telefono.isNotBlank() && telefono.matches("\\d+".toRegex())
    }

    private fun isValidEdad(edad: String): Boolean {
        // Aquí puedes implementar la lógica para verificar si la edad es válida
        return edad.isNotBlank() && edad.matches("\\d+".toRegex())
    }
}

