package com.example.messanger.presentation.fragment.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.messanger.R
import com.example.messanger.core.result.OperationResult
import com.example.messanger.databinding.FragmentLoginBinding
import com.example.messanger.presentation.utils.validateNumber
import com.redmadrobot.inputmask.MaskedTextChangedListener
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressBar = binding.progressBarLogIn
        val textViewError = binding.textViewErrorLogIn

        binding.buttonLogIn.setOnClickListener {
            val phoneNumber = binding.editTextLogIn.text.toString()
            val error = phoneNumber.validateNumber()

            if (error != "") {
                textViewError.visibility = View.VISIBLE

                textViewError.text = error
            } else {
                viewModel.performPhoneAuth(phoneNumber)
                progressBar.visibility = View.VISIBLE
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.loginFlow.collect { result ->
                when (result) {
                    is OperationResult.Success -> {
                        findNavController().navigate(R.id.action_loginFragment_to_otpFragment)

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
                        progressBar.visibility = View.GONE
                        textViewError.visibility = View.VISIBLE

                        when (result.exception) {

                        }

                        textViewError.text = "ОШИБКА"
                    }
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.userAuthFlow.collect { userAuth ->
                if (userAuth) {
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                }
            }
        }

        binding.editTextLogIn.addTextChangedListener(
            MaskedTextChangedListener(
                "+7-([000])-[000]-[00]-[00]",
                binding.editTextLogIn
            )
        )
    }
}