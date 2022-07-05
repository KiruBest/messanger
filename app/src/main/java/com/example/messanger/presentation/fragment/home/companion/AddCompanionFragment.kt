package com.example.messanger.presentation.fragment.home.companion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messanger.R
import com.example.messanger.databinding.FragmentAddCompanionBinding
import com.example.messanger.domain.core.AsyncOperationResult
import com.example.messanger.presentation.core.BaseFragment
import com.example.messanger.presentation.core.Constants.USER_DTO
import com.example.messanger.presentation.fragment.home.HomeViewModel
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddCompanionFragment : BaseFragment() {

    private lateinit var binding: FragmentAddCompanionBinding
    private val viewModel: HomeViewModel by viewModel()
    private lateinit var companionAdapter: CompanionAdapter

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

        val searchView: SearchView = toolbar.menu.findItem(R.id.app_bar_search).actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filter(newText)
                return true
            }
        })

        companionAdapter = CompanionAdapter(emptyList()) { userDto ->
            val bundle = bundleOf(USER_DTO to userDto)
            findNavController().navigate(R.id.action_addCompanionFragment_to_chatFragment, bundle)
        }

        val recyclerViewUsers = binding.recyclerViewUsers
        val linearLayoutManager = LinearLayoutManager(requireContext())

        recyclerViewUsers.apply {
            adapter = companionAdapter
            layoutManager = linearLayoutManager
        }

        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack(R.id.addCompanionFragment, true)
        }

        lifecycleScope.launchWhenCreated {
            viewModel.usersListFlow.collect { result ->
                when (result) {
                    is AsyncOperationResult.EmptyState -> {}
                    is AsyncOperationResult.Failure -> TODO()
                    is AsyncOperationResult.Loading -> {}
                    is AsyncOperationResult.Success -> {
                        companionAdapter.updateData(result.data)
                    }
                }
            }
        }

        viewModel.getUsersList()
    }
}