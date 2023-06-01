package com.example.messanger.presentation.fragment.call

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.messanger.R
import com.example.messanger.databinding.FragmentCallBinding
import com.example.messanger.presentation.fragment.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


class CallFragment : BaseFragment<CallViewModel, FragmentCallBinding>(
    layoutId = R.layout.fragment_call,
    viewBindingInflater = FragmentCallBinding::inflate
) {
    override val viewModel: CallViewModel by viewModel()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageButtonCallEnd.setOnClickListener {
            findNavController().popBackStack(R.id.chatFragment, true)
        }

        binding.imageButtonVideoCam.setOnClickListener {
            findNavController().navigate(R.id.videoCallFragment)
        }
    }
}