package com.example.messanger.presentation.fragment.home.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.messanger.R
import com.example.messanger.databinding.FragmentAccountSettingsBinding
import com.example.messanger.presentation.core.BaseFragment
import com.redmadrobot.inputmask.MaskedTextChangedListener

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
    }
}