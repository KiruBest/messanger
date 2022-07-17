package com.example.messanger.presentation.fragment.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.messanger.databinding.ChatLayoutItemBinding
import com.example.messanger.domain.model.ChatItemDto

class HomeChatAdapter(
    private var chatList: List<ChatItemDto>,
    private val onItemClickListener: (chatItemDto: ChatItemDto) -> Unit
) : RecyclerView.Adapter<HomeChatViewHolder>(), View.OnClickListener {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeChatViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ChatLayoutItemBinding.inflate(inflater, parent, false)

        binding.root.setOnClickListener(this)

        return HomeChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeChatViewHolder, position: Int) {
        val chatItem = chatList[position]
        holder.bind(chatItem)
        holder.itemView.tag = chatItem
    }

    override fun getItemCount(): Int = chatList.size

    override fun onClick(v: View?) {
        onItemClickListener.invoke(v?.tag as ChatItemDto)
    }

    fun update(newChatList: List<ChatItemDto>) {
        val diffCallback = HomeDiffUtilCallback(chatList, newChatList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        chatList = newChatList
        notifyDataSetChanged()
        diffResult.dispatchUpdatesTo(this)
    }
}