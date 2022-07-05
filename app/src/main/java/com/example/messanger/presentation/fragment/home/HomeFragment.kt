package com.example.messanger.presentation.fragment.home

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.messanger.R
import com.example.messanger.databinding.FragmentHomeBinding
import com.example.messanger.domain.core.AsyncOperationResult
import com.example.messanger.domain.core.UserState
import com.example.messanger.domain.model.UserDto
import com.example.messanger.presentation.core.BaseFragment
import com.example.messanger.presentation.fragment.authentication.LoginViewModel
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = binding.toolbar

        binding.buttonNewChat.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addCompanionFragment)
        }

        binding.chipChats.setOnClickListener {
            binding.chipChats.isChecked = true
            binding.chipCalls.isChecked = false
            binding.buttonNewChat.visibility = View.VISIBLE
        }

        binding.chipCalls.setOnClickListener {
            binding.chipCalls.isChecked = true
            binding.chipChats.isChecked = false
            binding.buttonNewChat.visibility = View.GONE
            viewModel.logOut()
        }

        lifecycleScope.launchWhenCreated {
            viewModel.userDtoFlow.collect { value ->
                when(value) {
                    is AsyncOperationResult.Success -> {
                        toolbar.menu.clear()
                        toolbar.inflateMenu(R.menu.account_menu)

                        toolbar.setOnMenuItemClickListener {
                            when (it.itemId) {
                                R.id.accountSettingsMenu -> {
                                    findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
                                    true
                                }
                                R.id.logOut -> {
                                    viewModel.logOut()
                                    true
                                }
                                else -> false
                            }
                        }

                        Glide.with(requireContext()).asDrawable()
                            .circleCrop()
                            .placeholder(R.drawable.ic_baseline_account_circle)
                            .load(value.data.avatarUrl)
                            .into(object : CustomTarget<Drawable>() {
                                override fun onResourceReady(
                                    resource: Drawable,
                                    transition: Transition<in Drawable>?
                                ) {
                                    toolbar.menu.findItem(R.id.accountMenu).icon = resource
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {
                                    toolbar.menu.findItem(R.id.accountMenu).icon = placeholder
                                }
                            })

                        binding.progressBar.visibility = View.GONE
                    }
                    is AsyncOperationResult.EmptyState -> TODO()
                    is AsyncOperationResult.Failure -> TODO()
                    is AsyncOperationResult.Loading -> binding.progressBar.visibility = View.VISIBLE
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.logOutFlow.collect { result ->
                when(result) {
                    is AsyncOperationResult.EmptyState -> binding.progressBar.visibility = View.GONE
                    is AsyncOperationResult.Failure -> {}
                    is AsyncOperationResult.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is AsyncOperationResult.Success -> {
                        binding.progressBar.visibility = View.GONE

                        if (result.data) findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
                    }
                }
            }
        }

        viewModel.getCurrentUser()
    }
}