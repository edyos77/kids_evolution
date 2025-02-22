package com.pargar.kidsevolution.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.pargar.kidsevolution.R
import com.pargar.kidsevolution.databinding.FragmentSignUpBinding
import com.pargar.kidsevolution.model.util.Resource
import com.pargar.kidsevolution.viewModel.SignUpViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignUpViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
        initListeners()
    }

    private fun initObservers() {
        viewModel.signUpState.observe(viewLifecycleOwner) { state ->
            when(state) {
                is Resource.Success -> {
                    handleLoading(isLoading = false)
                    activity?.onBackPressed()
                    Toast.makeText(
                        requireContext(),
                        "Sign up success",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Error -> {
                    handleLoading(isLoading = false)
                    Toast.makeText(
                        requireContext(),
                        state.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Loading -> handleLoading(isLoading = true)
                else -> Unit
            }
        }
    }

    private fun initListeners() {
        with(binding) {
            bBack.setOnClickListener{activity?.onBackPressed()}
            bSignUp.setOnClickListener {
                handleSignUp()
            }
        }
    }

    private fun handleSignUp() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        val nomPadre = binding.txtnombre.text.toString()
        val nomHijo = binding.txtnombreNiO.text.toString()
        val apellidos = binding.txtapellido.text.toString()
        val cedula = binding.txtcedula.text.toString()
        val telefono = binding.txttelefono.text.toString()
        val edad = binding.txtEdad.text.toString()

        viewModel.signUp(email, password, nomPadre, nomHijo, apellidos, cedula, telefono,edad)
    }

    private fun handleLoading(isLoading: Boolean) {
        with(binding) {
            if (isLoading) {
                bSignUp.text = ""
                bSignUp.isEnabled = false
                pbSignUp.visibility = View.VISIBLE
            } else {
                pbSignUp.visibility = View.GONE
                bSignUp.text = getString(R.string.login__signup_button)
                bSignUp.isEnabled = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}