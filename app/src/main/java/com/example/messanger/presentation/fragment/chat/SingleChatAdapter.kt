package com.example.messanger.presentation.fragment.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.messanger.databinding.SingleChatMessageLayoutBinding
import com.example.messanger.presentation.model.MessageUi

class SingleChatAdapter(
    private var messageList: List<MessageUi>,
    private val companionID: String
) : RecyclerView.Adapter<SingleChatViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleChatViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = SingleChatMessageLayoutBinding.inflate(layoutInflater, parent, false)
        return SingleChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SingleChatViewHolder, position: Int) {
        holder.bind(messageList[position], companionID)
    }

    override fun getItemCount(): Int = messageList.size

    fun updateMessageList(newMessageList: List<MessageUi>) {
        val diffUtilCallback = SingleChatDiffUtil(messageList, newMessageList)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)
        messageList = newMessageList
        diffResult.dispatchUpdatesTo(this)
    }
}