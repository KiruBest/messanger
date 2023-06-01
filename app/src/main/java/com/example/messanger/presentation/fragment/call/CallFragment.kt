package com.example.messanger.presentation.fragment.call

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.messanger.R
import com.example.messanger.core.constants.Constants
import com.example.messanger.core.enumeration.CallState
import com.example.messanger.core.enumeration.KEY_ARG_CALL_STATE
import com.example.messanger.databinding.FragmentCallBinding
import com.example.messanger.presentation.fragment.base.BaseFragment
import com.example.messanger.presentation.model.UserUi
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class CallFragment : BaseFragment<CallViewModel, FragmentCallBinding>(
    layoutId = R.layout.fragment_call,
    viewBindingInflater = FragmentCallBinding::inflate
) {
    override val viewModel: CallViewModel by viewModel(){
        parametersOf(requireArguments().get(KEY_ARG_CALL_STATE))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeTimer()
        observeCallState()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setNameAndImage()

        binding.imageButtonCallEnd.setOnClickListener {
            findNavController().popBackStack(R.id.chatFragment, true)
        }

        binding.imageButtonVideoCam.setOnClickListener{
            viewModel.changeState()
        }
    }

    private fun observeTimer(){
        lifecycleScope.launch{
            viewModel.timer.flowWithLifecycle(lifecycle,Lifecycle.State.RESUMED).collect{ currentTime ->
                binding.textViewCallTime.text = currentTime
            }
        }
    }

    private fun observeCallState(){
        lifecycleScope.launch{
            viewModel.callState.flowWithLifecycle(lifecycle,Lifecycle.State.STARTED).collect{ currentCallState ->
                when(currentCallState){
                    CallState.AUDIO -> setAudioState()
                    CallState.VIDEO -> setVideoState()
                }
            }
        }
    }

    private fun setAudioState(){
        binding.imageViewMyVideo.isVisible = false
        binding.imageViewUVideo.isVisible = false
        binding.imageButtonCallEnd.isVisible = true
        binding.imageButtonAddUser.isVisible = true
        binding.textViewCallName.isVisible = true
        binding.textViewCallTime.isVisible = true
        binding.imageViewCallAccount.isVisible = true
        binding.imageButtonVolumeUp.setImageResource(R.drawable.ic_baseline_volume_up_24)
        binding.imageButtonVideoCam.setImageResource(R.drawable.ic_baseline_videocam_24)


    }

    private fun setVideoState(){
        binding.imageViewMyVideo.isVisible = true
        binding.imageViewUVideo.isVisible = true

        binding.imageButtonCallEnd.isVisible = false
        binding.imageButtonAddUser.isVisible = false
        binding.textViewCallName.isVisible = false
        binding.textViewCallTime.isVisible = false
        binding.imageViewCallAccount.isVisible = false

        binding.imageButtonVolumeUp.setImageResource(R.drawable.ic_baseline_flip_camera_ios_24)
        binding.imageButtonVideoCam.setImageResource(R.drawable.ic_baseline_videocam_off_24)
    }

    private fun setNameAndImage(){
        (requireArguments().getSerializable(Constants.USER_DTO) as? UserUi)?.let { user ->
            binding.textViewCallName.text = user.fullName
            Glide.with(requireContext()).load(user.avatarUrl)
                .circleCrop()
                .placeholder(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_baseline_account_circle
                    )
                )
                .into(binding.imageViewCallAccount)
        }
    }
}