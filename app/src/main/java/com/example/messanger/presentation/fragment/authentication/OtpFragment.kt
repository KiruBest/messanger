package com.example.messanger.presentation.fragment.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.messanger.R
import com.example.messanger.databinding.FragmentLoginBinding
import com.example.messanger.databinding.FragmentOtpBinding
import com.example.messanger.domain.core.AsyncOperationResult
import com.example.messanger.presentation.core.validateCode
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class OtpFragment : Fragment() {

    private lateinit var binding: FragmentOtpBinding
    private val viewModel: LoginViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressBar = binding.progressBarOTP
        val editTextOTP = binding.editTextOTP
        val textViewError = binding.textViewErrorOTP

        binding.buttonLogOTP.setOnClickListener {
            val code = binding.editTextOTP.text.toString()
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
                when(result) {
                    is AsyncOperationResult.Success -> {
                        findNavController().navigate(R.id.action_otpFragment_to_homeFragment)

                        progressBar.visibility = View.INVISIBLE
                        textViewError.visibility = View.INVISIBLE
                    }
                    is AsyncOperationResult.EmptyState -> {
                        progressBar.visibility = View.INVISIBLE
                        textViewError.visibility = View.INVISIBLE
                    }
                    is AsyncOperationResult.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        textViewError.visibility = View.INVISIBLE
                    }
                    is AsyncOperationResult.Failure -> {
                        textViewError.text = "Неверный код"

                        progressBar.visibility = View.INVISIBLE
                        textViewError.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

}