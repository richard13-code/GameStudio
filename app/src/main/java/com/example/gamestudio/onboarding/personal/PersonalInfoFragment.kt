package com.example.gamestudio.onboarding.personal

import android.content.Intent
import android.app.DatePickerDialog
import com.example.gamestudio.ui.main.HomeActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.gamestudio.R
import com.example.gamestudio.core.FragmentCommunicator
import com.example.gamestudio.core.ResponseService
import com.example.gamestudio.databinding.FragmentPersonalInfoBinding
import java.util.*

class PersonalInfoFragment : Fragment() {

    private var _binding: FragmentPersonalInfoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PersonalInfoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.etBirthDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnSave.setOnClickListener {
            viewModel.saveProfile(
                firstName = binding.etFirstName.text.toString(),
                lastName = binding.etLastName.text.toString(),
                userName = binding.etUserName.text.toString(),
                phone = binding.etPhone.text.toString(),
                birthDate = binding.etBirthDate.text.toString()
            )
        }
    }

    private fun setupObservers() {
        viewModel.saveStatus.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ResponseService.Loading -> {
                    (activity as? FragmentCommunicator)?.manageLoader(isVisible = true)
                }
                is ResponseService.Success -> {
                    (activity as? FragmentCommunicator)?.manageLoader(isVisible = false)
                    Toast.makeText(context, "Perfil guardado correctamente", Toast.LENGTH_SHORT).show()
                    val intent = Intent(requireContext(), HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                is ResponseService.Error -> {
                    (activity as? FragmentCommunicator)?.manageLoader(false)
                    Toast.makeText(context, response.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        // Establecer fecha por defecto: Enero de 2008
        calendar.set(2008, Calendar.JANUARY, 1)

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val date = String.format(Locale.getDefault(), "%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
            binding.etBirthDate.setText(date)
        }, year, month, day).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}