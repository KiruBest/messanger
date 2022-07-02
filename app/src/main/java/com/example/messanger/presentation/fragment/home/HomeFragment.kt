package com.example.messanger.presentation.fragment.home

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.messanger.R
import com.example.messanger.databinding.FragmentHomeBinding
import com.example.messanger.presentation.fragment.authentication.LoginViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: LoginViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonNewChat.setOnClickListener {
            viewModel.logOut()
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
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

        binding.toolbar.inflateMenu(R.menu.account_menu)

        Glide.with(requireContext()).asDrawable()
            .circleCrop()
            .placeholder(R.drawable.ic_baseline_account_circle)
            .load("https://userpic.fishki.net/2020/08/22/1618868/da70406ad3ef69590e4410f9bf3d1964.jpg")
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    binding.toolbar.menu.findItem(R.id.accountMenu).icon = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    binding.toolbar.menu.findItem(R.id.accountMenu).icon = placeholder
                }
            })

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.accountSettingsMenu -> {
                    findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
                    true
                }
                else -> false
            }
        }
    }
}