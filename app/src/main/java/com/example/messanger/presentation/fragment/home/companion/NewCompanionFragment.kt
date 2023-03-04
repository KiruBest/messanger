package com.example.messanger.presentation.fragment.home.companion

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messanger.R
import com.example.messanger.core.constants.Constants.COMPANION_ID
import com.example.messanger.core.result.OperationResult
import com.example.messanger.databinding.FragmentAddCompanionBinding
import com.example.messanger.presentation.fragment.base.BaseFragment
import com.example.messanger.presentation.fragment.home.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewCompanionFragment : BaseFragment<HomeViewModel, FragmentAddCompanionBinding>(
    layoutId = R.layout.fragment_add_companion,
    viewBindingInflater = FragmentAddCompanionBinding::inflate
) {
    //todo Необходимо разделить viewModel, так делать не стоит
    override val viewModel: HomeViewModel by viewModel()

    //todo Не использовать lateinit никогда кроме DI, плюс в такой реализации скорее всего утечки памяти
    private lateinit var companionAdapter: CompanionAdapter

    private val onQueryTextListener by lazy {
        object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filter(newText)
                return true
            }
        }
    }

    private val searchView: SearchView
        get() =
            binding.toolbar.menu.findItem(R.id.app_bar_search).actionView as SearchView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = binding.toolbar

        //todo Сто процентов тут утечка))) всегда такие штуки нужно отвязывать в OnDestroyView
        searchView.setOnQueryTextListener(onQueryTextListener)

        companionAdapter = CompanionAdapter { userDto ->
            val bundle = bundleOf(COMPANION_ID to userDto.id)
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
                /* TODO (в целом полная фигня получилась с таким подходом, можно было сделать проще
                надо обрабатывать OperationResult во вьюмодели и отдавать готовое флоу(или LiveData))
                Как видишь приходится лишний раз очищать RecyclerView, что полный бред и путает капец*/
                when (result) {
                    is OperationResult.Empty -> {
                        binding.textViewEmpty.isVisible = true
                        binding.progressBar.isVisible = false
                        companionAdapter.updateData(emptyList())
                    }
                    is OperationResult.Error -> TODO()
                    is OperationResult.Loading -> {
                        binding.textViewEmpty.isVisible = false
                        binding.progressBar.isVisible = true
                        companionAdapter.updateData(emptyList())
                    }
                    is OperationResult.Success -> {
                        binding.textViewEmpty.isVisible = false
                        binding.progressBar.isVisible = false
                        companionAdapter.updateData(result.data)
                    }
                }
            }
        }
    }
}