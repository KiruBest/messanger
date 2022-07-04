package com.example.messanger.presentation.fragment.authentication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.messanger.R
import com.example.messanger.databinding.FragmentLoginBinding
import com.example.messanger.domain.core.AsyncOperationResult
import com.example.messanger.presentation.core.validateNumber
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.redmadrobot.inputmask.MaskedTextChangedListener

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

        Log.i("User", FirebaseAuth.getInstance().currentUser?.uid.toString())

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
                    is AsyncOperationResult.Success -> {
                        findNavController().navigate(R.id.action_loginFragment_to_otpFragment)

                        progressBar.visibility = View.GONE
                        textViewError.visibility = View.GONE
                    }
                    is AsyncOperationResult.EmptyState -> {
                        progressBar.visibility = View.GONE
                        textViewError.visibility = View.GONE
                    }
                    is AsyncOperationResult.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        textViewError.visibility = View.GONE
                    }
                    is AsyncOperationResult.Failure -> {
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
                "+7 ([000]) [000]-[00]-[00]",
                binding.editTextLogIn
            )
        )
    }
}