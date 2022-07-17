package com.example.messanger.presentation.fragment.home.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.messanger.R
import com.example.messanger.databinding.FragmentSettingsBinding
import com.example.messanger.presentation.core.BaseFragment
import com.example.messanger.presentation.core.Constants
import com.example.messanger.presentation.core.Constants.USER_DTO

class SettingsFragment : BaseFragment() {

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userId = requireArguments().getString(USER_DTO)

        binding.buttonAccountSettings.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_accountSettings, bundleOf(
                Constants.USER_DTO to userId)
            )
        }
    }

}