package com.example.messanger.presentation.fragment.home.settings

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.messanger.R
import com.example.messanger.core.result.OperationResult
import com.example.messanger.databinding.FragmentSettingsBinding
import com.example.messanger.presentation.fragment.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : BaseFragment<AccountSettingsViewModel, FragmentSettingsBinding>(
    layoutId = R.layout.fragment_settings,
    viewBindingInflater = FragmentSettingsBinding::inflate
) {
    override val viewModel: AccountSettingsViewModel by viewModel()

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
                    is OperationResult.Empty -> {}
                    is OperationResult.Error -> {}
                    is OperationResult.Loading -> {}
                    is OperationResult.Success -> {
                        val userDto = it.data

                        binding.textViewName.text = userDto.fullName

                        Glide.with(requireContext()).load(userDto.avatarUrl)
                            .circleCrop().placeholder(R.drawable.ic_baseline_account_circle)
                            .into(binding.imageViewSettings)
                    }
                }
            }
        }
    }

}