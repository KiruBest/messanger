package com.example.messanger.presentation.fragment.home

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.messanger.R
import com.example.messanger.core.constants.Constants.COMPANION_ID
import com.example.messanger.core.constants.Constants.USER_DTO
import com.example.messanger.core.result.OperationResult
import com.example.messanger.databinding.FragmentHomeBinding
import com.example.messanger.presentation.fragment.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>(
    viewBindingInflater = FragmentHomeBinding::inflate,
    layoutId = R.layout.fragment_home
) {
    override val viewModel: HomeViewModel by viewModel()

    private lateinit var _adapter: HomeChatAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = binding.toolbar

        _adapter = HomeChatAdapter(emptyList()) { chatItemDto ->
            val userDto = chatItemDto.mapToUserDto()
            val bundle = bundleOf(COMPANION_ID to userDto.id)
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
                /* todo (в целом полная фигня получилась с таким подходом, можно было сделать проще
                надо обрабатывать OperationResult во вьюмодели и отдавать готовое флоу(или LiveData)) */
                when (value) {
                    is OperationResult.Success -> {
                        viewModel.getChats()

                        toolbar.menu.clear()
                        toolbar.inflateMenu(R.menu.account_menu)

                        toolbar.setOnMenuItemClickListener {
                            when (it.itemId) {
                                R.id.accountSettingsMenu -> {
                                    findNavController().navigate(
                                        R.id.action_homeFragment_to_settingsFragment, bundleOf(
                                            USER_DTO to value.data.id
                                        )
                                    )
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
                    is OperationResult.Empty -> TODO()
                    is OperationResult.Error -> TODO()
                    is OperationResult.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.buttonNewChat.visibility = View.GONE
                    }
                }
            }
        }

        viewModel.chatListFlow.observe(viewLifecycleOwner) { result ->
            when (result) {
                is OperationResult.Empty -> {}
                is OperationResult.Error -> {}
                is OperationResult.Loading -> {}
                is OperationResult.Success -> {
                    _adapter.update(result.data)
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.logOutFlow.collect { result ->
                when (result) {
                    is OperationResult.Empty -> binding.progressBar.visibility = View.GONE
                    is OperationResult.Error -> {}
                    is OperationResult.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is OperationResult.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val canNavigateToLogin = result.data
                                && findNavController().currentDestination?.id == R.id.homeFragment

                        if (canNavigateToLogin) {
                            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
                        }
                    }
                }
            }
        }
    }
}