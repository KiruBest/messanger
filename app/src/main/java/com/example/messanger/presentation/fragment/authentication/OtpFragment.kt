package com.example.messanger.presentation.fragment.authentication

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.messanger.R
import com.example.messanger.core.result.OperationResult
import com.example.messanger.databinding.FragmentOtpBinding
import com.example.messanger.presentation.fragment.base.BaseFragment
import com.example.messanger.presentation.utils.validateCode
import org.koin.androidx.viewmodel.ext.android.viewModel


class OtpFragment : BaseFragment<LoginViewModel, FragmentOtpBinding>(
    viewBindingInflater = FragmentOtpBinding::inflate,
    layoutId = R.layout.fragment_otp
) {
    override val viewModel: LoginViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressBar = binding.progressBarOTP
        val pinViewOTP = binding.pinViewOTP
        val textViewError = binding.textViewErrorOTP

        binding.buttonLogOTP.setOnClickListener {
            val code = pinViewOTP.text.toString()
            val error = code.validateCode()

            if (error != "") {
                textViewError.visibility = View.VISIBLE
                textViewError.text = error
            } else {
                viewModel.sentAuthCode(code)
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.codeSentFlow.collect { result ->
                when (result) {
                    is OperationResult.Success -> {
                        findNavController().navigate(R.id.action_otpFragment_to_homeFragment)

                        progressBar.visibility = View.GONE
                        textViewError.visibility = View.GONE
                    }
                    is OperationResult.Empty -> {
                        progressBar.visibility = View.GONE
                        textViewError.visibility = View.GONE
                    }
                    is OperationResult.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        textViewError.visibility = View.GONE
                    }
                    is OperationResult.Error -> {
                        binding.pinViewOTP.text?.clear()

                        textViewError.text = "Неверный код"

                        progressBar.visibility = View.GONE
                        textViewError.visibility = View.VISIBLE
                    }
                }
            }
        }

        object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.textViewTimer.text = (millisUntilFinished / 1000).toString()
            }
            override fun onFinish() {
                binding.textViewTimer.text = "Код отправлен повторно!"
            }
        }.start()


    }

}