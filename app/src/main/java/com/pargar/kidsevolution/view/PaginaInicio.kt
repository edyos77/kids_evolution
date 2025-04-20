package com.pargar.kidsevolution.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.navigation.fragment.findNavController
import com.pargar.kidsevolution.R
import com.pargar.kidsevolution.databinding.FragmentPaginaInicioBinding

class PaginaInicio : Fragment() {

    private var _binding: FragmentPaginaInicioBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPaginaInicioBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }


    private fun initListeners() {
        with(binding) {
            // Acción al dar clic // Regresar al fragmento anterior
            //btnBack.setOnClickListener {parentFragmentManager.popBackStack()}
            bBack.setOnClickListener {
                findNavController().navigate(R.id.action_paginaInicio_to_loginFragment)}
            btnMiniGames.setOnClickListener{
                findNavController().navigate(R.id.action_paginaInicio_to_homeFragment3)}
            //btnAR.setOnClickListener{
            //    findNavController().navigate(R.id.action_paginaInicio_to_reality)}
            btnAR.setOnClickListener {
                findNavController().navigate(R.id.action_paginaInicio_to_reality)
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}