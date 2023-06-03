package com.example.messanger.presentation.fragment.chat.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.messanger.databinding.ItemSingleChatMessageCompanionBinding
import com.example.messanger.databinding.ItemSingleChatMessageMeBinding
import com.example.messanger.presentation.model.MessageUi

class SingleChatAdapter(
    private val companionID: String
) : RecyclerView.Adapter<MessageViewHolder>() {
    companion object {
        private const val COMPANION_VIEW_HOLDER = 0
        private const val ME_VIEW_HOLDER = 1
    }

    private var messageList: List<MessageUi> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return if (viewType == COMPANION_VIEW_HOLDER) {
            val binding =
                ItemSingleChatMessageCompanionBinding.inflate(layoutInflater, parent, false)
            SingleChatFromCompanionViewHolder(binding)
        } else {
            val binding = ItemSingleChatMessageMeBinding.inflate(layoutInflater, parent, false)
            SingleChatFromMeViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messageList[position])
    }

    override fun getItemCount(): Int = messageList.size

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].isCompanion(companionID)) {
            COMPANION_VIEW_HOLDER
        } else {
            ME_VIEW_HOLDER
        }
    }

    fun updateMessageList(newMessageList: List<MessageUi>) {
        val diffUtilCallback = SingleChatDiffUtil(messageList, newMessageList)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)
        messageList = newMessageList
        diffResult.dispatchUpdatesTo(this)
    }
}