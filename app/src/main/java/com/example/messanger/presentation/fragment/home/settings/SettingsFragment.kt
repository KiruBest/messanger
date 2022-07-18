package com.example.messanger.presentation.fragment.home.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.messanger.R
import com.example.messanger.databinding.FragmentSettingsBinding
import com.example.messanger.domain.core.AsyncOperationResult
import com.example.messanger.presentation.core.BaseFragment
import com.example.messanger.presentation.core.CompanionTitleBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : BaseFragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val viewModel: AccountSettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonAccountSettings.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_accountSettings)
        }

        binding.toolbarSettings.setNavigationOnClickListener {
            findNavController().popBackStack(R.id.settingsFragment, true)
        }

        lifecycleScope.launchWhenCreated {
            viewModel.userDtoFlow.collect {
                when (it) {
                    is AsyncOperationResult.EmptyState -> {}
                    is AsyncOperationResult.Failure -> {}
                    is AsyncOperationResult.Loading -> {}
                    is AsyncOperationResult.Success -> {
                        val userDto = it.data

                        binding.textViewName.text =
                            CompanionTitleBuilder(userDto, requireContext()).getTitle()

                        Glide.with(requireContext()).load(userDto.avatarUrl)
                            .circleCrop().placeholder(R.drawable.ic_baseline_account_circle)
                            .into(binding.imageViewSettings)
                    }
                }
            }
        }

        viewModel.getCurrentUser()
    }

}