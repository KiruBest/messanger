package com.example.messanger.presentation.fragment.chat

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.messanger.R
import com.example.messanger.databinding.FragmentChatBinding
import com.example.messanger.domain.core.AsyncOperationResult
import com.example.messanger.domain.model.UserDto
import com.example.messanger.presentation.core.BaseFragment
import com.example.messanger.presentation.core.CompanionTitleBuilder
import com.example.messanger.presentation.core.Constants.COMPANION_ID
import com.example.messanger.presentation.core.Constants.USER_DTO
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChatFragment : BaseFragment() {

    private lateinit var binding: FragmentChatBinding
    private lateinit var companionID: String
    private lateinit var singleChatAdapter: SingleChatAdapter

    private val viewModel: ChatViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = binding.toolbar

        toolbar.findViewById<ImageButton>(R.id.navIcon).setOnClickListener {
            findNavController().popBackStack(R.id.chatFragment, true)
        }

        companionID = requireArguments().getString(COMPANION_ID)!!

        val recyclerView = binding.recyclerViewMessages

        singleChatAdapter = SingleChatAdapter(emptyList(), companionID)
        val linearLayoutManager = LinearLayoutManager(requireContext())

        linearLayoutManager.reverseLayout

        recyclerView.apply {
            adapter = singleChatAdapter
            layoutManager = linearLayoutManager
        }

        recyclerView.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (linearLayoutManager.findLastVisibleItemPosition() != singleChatAdapter.itemCount - 1) {
                recyclerView.smoothScrollBy(0, oldBottom - bottom)
            }
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy < 0) {
                    val v = requireActivity().currentFocus
                    val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v?.windowToken, 0)
                }
            }
        })

        binding.imageViewSendMessage.setOnClickListener {
            val message = binding.editTextSendMessage.text.toString()
            viewModel.sendMessage(message, companionID)
            binding.editTextSendMessage.text.clear()
            recyclerView.scrollToPosition(singleChatAdapter.itemCount - 1)
        }

        lifecycleScope.launchWhenCreated {
            viewModel.messageListFlow.collect { result ->
                when (result) {
                    is AsyncOperationResult.EmptyState -> {}
                    is AsyncOperationResult.Failure -> TODO()
                    is AsyncOperationResult.Loading -> {}
                    is AsyncOperationResult.Success -> {
                        val messages = result.data
                        singleChatAdapter.updateMessageList(messages)
                        recyclerView.scrollToPosition(singleChatAdapter.itemCount - 1)
                        messages.forEach {
                            if (!it.seen) viewModel.readMessage(companionID, it.id)
                        }
                    }
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.companionFlow.collect { result ->
                when(result) {
                    is AsyncOperationResult.EmptyState -> {}
                    is AsyncOperationResult.Failure -> {}
                    is AsyncOperationResult.Loading -> {}
                    is AsyncOperationResult.Success -> {
                        val companion = result.data
                        toolbar.findViewById<TextView>(R.id.companionName).text =
                            CompanionTitleBuilder(companion, requireContext()).getTitle()

                        toolbar.findViewById<TextView>(R.id.companionStatus).text = companion.status

                        Glide.with(requireContext())
                            .load(companion.avatarUrl)
                            .circleCrop()
                            .placeholder(R.drawable.ic_baseline_account_circle)
                            .into(toolbar.findViewById(R.id.companionAvatar))
                    }
                }
            }
        }

        viewModel.getCompanionById(companionID)
        viewModel.getMessages(companionID)
    }
}