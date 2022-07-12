package com.example.messanger.presentation.fragment.home

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.messanger.R
import com.example.messanger.databinding.FragmentHomeBinding
import com.example.messanger.domain.core.AsyncOperationResult
import com.example.messanger.domain.core.UserState
import com.example.messanger.domain.model.UserDto
import com.example.messanger.presentation.core.BaseFragment
import com.example.messanger.presentation.core.Constants
import com.example.messanger.presentation.fragment.authentication.LoginViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var _adapter: HomeChatAdapter

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

        _adapter = HomeChatAdapter(emptyList()) { chatItemDto ->
            val userDto = chatItemDto.mapToUserDto()
            val bundle = bundleOf(Constants.USER_DTO to userDto)
            findNavController().navigate(R.id.action_homeFragment_to_chatFragment, bundle)
        }

        val linerLayoutManager = LinearLayoutManager(requireContext())

        binding.recyclerView.apply {
            adapter = _adapter
            layoutManager = linerLayoutManager
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
        }

        lifecycleScope.launchWhenCreated {
            viewModel.userDtoFlow.collect { value ->
                when(value) {
                    is AsyncOperationResult.Success -> {
                        viewModel.getChats()

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

                        binding.buttonNewChat.visibility = View.VISIBLE

                        binding.buttonNewChat.setOnClickListener {
                            findNavController().navigate(R.id.action_homeFragment_to_addCompanionFragment)
                        }

                        binding.progressBar.visibility = View.GONE
                    }
                    is AsyncOperationResult.EmptyState -> TODO()
                    is AsyncOperationResult.Failure -> TODO()
                    is AsyncOperationResult.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.buttonNewChat.visibility = View.GONE
                    }
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.chatListFlow.collect { result ->
                when(result) {
                    is AsyncOperationResult.EmptyState -> {}
                    is AsyncOperationResult.Failure -> {}
                    is AsyncOperationResult.Loading -> {}
                    is AsyncOperationResult.Success -> {
                        _adapter.update(result.data)
                    }
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