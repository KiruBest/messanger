package com.example.messanger.presentation.fragment.home.settings

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.messanger.R
import com.example.messanger.databinding.ChatLayoutItemBinding
import com.example.messanger.domain.model.ChatItemDto
import com.example.messanger.presentation.core.CompanionTitleBuilderFromChatItemDto
import com.example.messanger.presentation.core.asDatHourMinute

class HomeChatViewHolder(binding: ChatLayoutItemBinding): RecyclerView.ViewHolder(binding.root) {
    private val lastMessage = binding.lastMessage
    private val userAvatar = binding.userAvatar
    private val userName = binding.userName
    private val time = binding.time
    private val noReadMessageCount = binding.noReadMessageCount
    private val status = binding.status
    private val context = binding.root.context

    fun bind(chatItemDto: ChatItemDto) {
        lastMessage.text = chatItemDto.text
        userName.text = CompanionTitleBuilderFromChatItemDto(chatItemDto, context).getTitle()
        time.text = chatItemDto.timestamp.asDatHourMinute()
        status.background = ContextCompat.getDrawable(context, R.color.aqua_color)
        status.visibility = View.VISIBLE

        Glide.with(context).load(chatItemDto.avatarUrl)
            .circleCrop()
            .placeholder(ContextCompat.getDrawable(context, R.drawable.ic_baseline_account_circle))
            .into(userAvatar)
    }
}