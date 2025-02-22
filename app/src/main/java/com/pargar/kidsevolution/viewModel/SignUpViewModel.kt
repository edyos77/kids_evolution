package com.pargar.kidsevolution.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pargar.kidsevolution.model.domain.usecase.FirebaseSignUpUseCase
import com.pargar.kidsevolution.model.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: FirebaseSignUpUseCase
) :  ViewModel() {

    private val _signUpState: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val signUpState: LiveData<Resource<Boolean>>
        get() = _signUpState

    fun signUp(email: String, password: String, nomPadre: String, nomHijo: String, apellidos: String, cedula: String, telefono: String,edad: String) {
        viewModelScope.launch {
            signUpUseCase(email, password, nomPadre, nomHijo, apellidos, cedula, telefono,edad).onEach { state ->
                _signUpState.value = state
            }.launchIn(viewModelScope)
        }
    }

}