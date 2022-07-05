package com.example.messanger.presentation.fragment.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.messanger.R
import com.example.messanger.databinding.FragmentChatBinding
import com.example.messanger.domain.model.UserDto
import com.example.messanger.presentation.core.BaseFragment
import com.example.messanger.presentation.core.CompanionTitleBuilder
import com.example.messanger.presentation.core.Constants.USER_DTO
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChatFragment : BaseFragment() {

    private lateinit var binding: FragmentChatBinding
    private lateinit var companion: UserDto
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

        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack(R.id.chatFragment, true)
        }

        companion = requireArguments().getSerializable(USER_DTO) as UserDto

        val recyclerView = binding.recyclerViewMessages

        singleChatAdapter = SingleChatAdapter(emptyList(), companion.id)
        val linearLayoutManager = LinearLayoutManager(requireContext())

        recyclerView.apply {
            adapter = singleChatAdapter
            layoutManager = linearLayoutManager
        }

        toolbar.findViewById<TextView>(R.id.companionName).text =
            CompanionTitleBuilder(companion, requireContext()).getTitle()

        toolbar.findViewById<TextView>(R.id.companionStatus).text = companion.status

        Glide.with(requireContext())
            .load(companion.avatarUrl)
            .circleCrop()
            .placeholder(R.drawable.ic_baseline_account_circle)
            .into(toolbar.findViewById(R.id.companionAvatar))

        binding.imageViewSendMessage.setOnClickListener {
            val message = binding.editTextSendMessage.text.toString()
            viewModel.sendMessage(message, companion.id)
            binding.editTextSendMessage.text.clear()
        }
    }
}