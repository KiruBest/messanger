package com.example.messanger.presentation.fragment.home.settings

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.messanger.databinding.FragmentAccountSettingsBinding
import com.example.messanger.presentation.core.BaseFragment
import com.redmadrobot.inputmask.MaskedTextChangedListener
import java.text.SimpleDateFormat
import java.util.*

class AccountSettings : BaseFragment() {

    private lateinit var binding: FragmentAccountSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editTextPhone.addTextChangedListener(
            MaskedTextChangedListener(
                "+7 ([000]) [000]-[00]-[00]",
                binding.editTextPhone
            )
        )

        val cal = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLable(cal)
        }

        binding.editTextDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun updateLable(cal: Calendar?) {
        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        if (cal != null) {
            binding.editTextDate.setText(sdf.format(cal.time))
        }
    }
}