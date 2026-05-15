package com.example.gamestudio.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.gamestudio.core.ResponseService
import com.example.gamestudio.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        setupObservers()
        viewModel.fetchUserProfile()
    }

    private fun setupClickListeners() {
        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            requireActivity().finish()
        }

        binding.btnExit.setOnClickListener {
            requireActivity().finishAffinity()
        }

        binding.tilUsername.setEndIconOnClickListener {
            val newUsername = binding.etUsername.text.toString().trim()
            if (newUsername.isNotEmpty()) {
                viewModel.updateUsername(newUsername)
            }
        }
    }

    private fun setupObservers() {
        viewModel.userProfile.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ResponseService.Loading -> {
                    // Mostrar algún mini-loader si es necesario
                }
                is ResponseService.Success -> {
                    val profile = response.data
                    binding.etUsername.setText(profile.userName)
                    // Obtenemos el email directamente de Firebase Auth
                    binding.etEmail.setText(FirebaseAuth.getInstance().currentUser?.email ?: "No disponible")
                    binding.etPhone.setText(profile.phone)
                }
                is ResponseService.Error -> {
                    val errorMsg = "Error al cargar perfil: ${response.error}"
                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                    binding.etUsername.setHint(errorMsg)
                }
            }
        }

        viewModel.updateStatus.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ResponseService.Loading -> {
                    binding.tilUsername.isEnabled = false
                }
                is ResponseService.Success -> {
                    binding.tilUsername.isEnabled = true
                    Toast.makeText(context, "Nombre de usuario actualizado", Toast.LENGTH_SHORT).show()
                }
                is ResponseService.Error -> {
                    binding.tilUsername.isEnabled = true
                    Toast.makeText(context, response.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}