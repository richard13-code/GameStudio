package com.example.gamestudio.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.gamestudio.MainActivity
import com.example.gamestudio.core.ResponseService
import com.example.gamestudio.databinding.FragmentProfileBinding
import com.example.gamestudio.onboarding.personal.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()
    private val auth = FirebaseAuth.getInstance()

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
        observeState()
        viewModel.fetchUserProfile()
    }

    private fun setupClickListeners() {
        binding.btnCerrarSesion.setOnClickListener { logout() }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userProfileState.collect { response ->
                    when (response) {
                        is ResponseService.Loading ->
                            binding.progressBar.visibility = View.VISIBLE
                        is ResponseService.Success -> {
                            binding.progressBar.visibility = View.GONE
                            updateUI(response.data)
                        }
                        is ResponseService.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(requireContext(), response.error, Toast.LENGTH_SHORT)
                                .show()
                        }
                        null -> {}
                    }
                }
            }
        }
    }

    private fun updateUI(user: UserProfile) {
        binding.tvUserName.text = "@${user.userName}"
        binding.tvUserEmail.text = auth.currentUser?.email ?: user.email
        binding.tvDisplayFullName.text = "${user.firstName} ${user.lastName}"
        binding.tvDisplayPhone.text = user.phone
        if (user.firstName.isNotEmpty()) {
            binding.tvAvatarInitial.text = user.firstName.take(1).uppercase()
        }
    }

    private fun logout() {
        auth.signOut()
        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}