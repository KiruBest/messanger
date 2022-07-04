package com.example.messanger.presentation.fragment.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.messanger.R
import com.example.messanger.databinding.FragmentAddCompanionBinding
import com.example.messanger.domain.core.AsyncOperationResult
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddCompanionFragment : Fragment() {

    private lateinit var binding: FragmentAddCompanionBinding
    private val viewModel: HomeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddCompanionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = binding.toolbar

        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack(R.id.addCompanionFragment, true)
        }

        lifecycleScope.launchWhenCreated {
            viewModel.usersListFlow.collect { result ->
                when (result) {
                    is AsyncOperationResult.EmptyState -> TODO()
                    is AsyncOperationResult.Failure -> TODO()
                    is AsyncOperationResult.Loading -> {}
                    is AsyncOperationResult.Success -> {
                        Log.i("USERS", result.data.toString())
                    }
                }
            }
        }

        viewModel.getUsersList()
    }
}