package com.example.messanger.presentation.fragment.call

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.messanger.R
import com.example.messanger.databinding.FragmentCallBinding
import com.example.messanger.databinding.FragmentVideoCallBinding
import com.example.messanger.presentation.fragment.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideoCallFragment : BaseFragment<VideoCallViewModel, FragmentVideoCallBinding>(
    layoutId = R.layout.fragment_video_call,
    viewBindingInflater = FragmentVideoCallBinding::inflate
) {

    override val viewModel: VideoCallViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageButtonCallEnd.setOnClickListener {
            findNavController().popBackStack(R.id.chatFragment, true)
        }

        binding.imageButtonVideoCam.setOnClickListener {
            findNavController().navigate(R.id.callFragment)
        }
    }
}