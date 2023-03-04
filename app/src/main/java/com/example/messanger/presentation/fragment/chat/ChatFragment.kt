package com.example.messanger.presentation.fragment.chat

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.messanger.R
import com.example.messanger.core.constants.Constants
import com.example.messanger.core.result.OperationResult
import com.example.messanger.databinding.FragmentChatBinding
import com.example.messanger.presentation.fragment.base.BaseFragment
import com.example.messanger.presentation.fragment.chat.list.SingleChatAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ChatFragment : BaseFragment<ChatViewModel, FragmentChatBinding>(
    viewBindingInflater = FragmentChatBinding::inflate,
    layoutId = R.layout.fragment_chat,
) {
    override val viewModel: ChatViewModel by viewModel {
        parametersOf(requireArguments().get(Constants.COMPANION_ID))
    }

    private val singleChatAdapter by lazy {
        SingleChatAdapter(viewModel.companionID)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            navIcon.setOnClickListener {
                findNavController().popBackStack(R.id.chatFragment, true)
            }

            recyclerViewMessages.apply {
                val linearLayoutManager = LinearLayoutManager(requireContext()).apply {
                    reverseLayout = false
                }
                adapter = singleChatAdapter
                layoutManager = linearLayoutManager

                addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
                    if (linearLayoutManager.findLastVisibleItemPosition() != singleChatAdapter.itemCount - 1) {
                        recyclerViewMessages.smoothScrollBy(0, oldBottom - bottom)
                    }
                }

                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        if (dy < 0) {
                            val v = requireActivity().currentFocus
                            val imm = requireActivity().getSystemService(
                                Context.INPUT_METHOD_SERVICE
                            ) as InputMethodManager
                            imm.hideSoftInputFromWindow(v?.windowToken, 0)
                        }
                    }
                })
            }

            imageViewSendMessage.setOnClickListener {
                val message = editTextSendMessage.text.toString()
                viewModel.sendMessage(message)
                editTextSendMessage.text.clear()
                recyclerViewMessages.scrollToPosition(singleChatAdapter.itemCount - 1)
            }

            lifecycleScope.launchWhenCreated {
                viewModel.messageListFlow.collect { result ->
                    when (result) {
                        is OperationResult.Empty -> {}
                        is OperationResult.Error -> TODO()
                        is OperationResult.Loading -> {}
                        is OperationResult.Success -> {
                            val messages = result.data
                            singleChatAdapter.updateMessageList(messages)
                            recyclerViewMessages.scrollToPosition(singleChatAdapter.itemCount - 1)
                            messages.forEach {
                                if (!it.seen) viewModel.readMessage(it.id)
                            }
                        }
                    }
                }
            }

            lifecycleScope.launchWhenCreated {
                viewModel.companionFlow.collect { result ->
                    when (result) {
                        is OperationResult.Empty -> {}
                        is OperationResult.Error -> {}
                        is OperationResult.Loading -> {}
                        is OperationResult.Success -> {
                            val companion = result.data
                            toolbar.findViewById<TextView>(R.id.companionName).text =
                                companion.fullName

                            toolbar.findViewById<TextView>(R.id.companionStatus).text =
                                companion.status.status

                            Glide.with(requireContext())
                                .load(companion.avatarUrl)
                                .circleCrop()
                                .placeholder(R.drawable.ic_baseline_account_circle)
                                .into(toolbar.findViewById(R.id.companionAvatar))
                        }
                    }
                }
            }
        }
    }
}