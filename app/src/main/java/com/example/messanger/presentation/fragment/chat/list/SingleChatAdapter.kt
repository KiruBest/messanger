package com.example.messanger.presentation.fragment.chat.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.messanger.databinding.SingleChatMessageLayoutBinding
import com.example.messanger.presentation.model.MessageUi

class SingleChatAdapter(
    private val companionID: String
) : RecyclerView.Adapter<SingleChatFromMeViewHolder>() {
    private var messageList: List<MessageUi> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleChatFromMeViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = SingleChatMessageLayoutBinding.inflate(layoutInflater, parent, false)
        return SingleChatFromMeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SingleChatFromMeViewHolder, position: Int) {
        holder.bind(messageList[position], companionID)
    }

    override fun getItemCount(): Int = messageList.size

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    fun updateMessageList(newMessageList: List<MessageUi>) {
        val diffUtilCallback = SingleChatDiffUtil(messageList, newMessageList)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)
        messageList = newMessageList
        diffResult.dispatchUpdatesTo(this)
    }
}

enum class MessageType {
    FROM_ME, FROM_COMPANION
}