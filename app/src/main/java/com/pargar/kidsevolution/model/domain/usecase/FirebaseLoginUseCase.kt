package com.pargar.kidsevolution.model.domain.usecase

import com.pargar.kidsevolution.model.domain.repository.AuthRepository
import com.pargar.kidsevolution.model.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FirebaseLoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(email: String, password: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        val loggedSuccessfully = authRepository.login(email,password)
        if (loggedSuccessfully) {
            emit(Resource.Success(true))
            emit(Resource.Finished)
        } else {
            emit(Resource.Error("Usuario o contraseña incorrecta"))
            emit(Resource.Finished)
        }
    }
}