package com.example.messanger.presentation.fragment.home

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.messanger.R
import com.example.messanger.databinding.ChatLayoutItemBinding
import com.example.messanger.domain.core.UserState
import com.example.messanger.domain.model.ChatItemDto
import com.example.messanger.presentation.core.CompanionTitleBuilderFromChatItemDto
import com.example.messanger.presentation.core.LastMessageBuilder
import com.example.messanger.presentation.core.asDatHourMinute

class HomeChatViewHolder(binding: ChatLayoutItemBinding) : RecyclerView.ViewHolder(binding.root) {
    private val lastMessage = binding.lastMessage
    private val userAvatar = binding.userAvatar
    private val userName = binding.userName
    private val time = binding.time
    private val noReadMessageCount = binding.noReadMessageCount
    private val status = binding.status
    private val context = binding.root.context

    fun bind(chatItemDto: ChatItemDto) {
        lastMessage.text = LastMessageBuilder(chatItemDto).getLastMessageSingleChat()

        userName.text = CompanionTitleBuilderFromChatItemDto(chatItemDto, context).getTitle()
        time.text = chatItemDto.timestamp.asDatHourMinute()
        status.background = ContextCompat.getDrawable(context, R.color.aqua_color)

        if (chatItemDto.noSeenMessageCount != 0 && chatItemDto.userID == chatItemDto.from) {
            noReadMessageCount.text = chatItemDto.noSeenMessageCount.toString()
            noReadMessageCount.visibility = View.VISIBLE
        } else {
            noReadMessageCount.visibility = View.GONE
        }

        when (chatItemDto.status) {
            UserState.ONLINE.state -> status.visibility = View.VISIBLE
            else -> status.visibility = View.GONE
        }

        Glide.with(context).load(chatItemDto.avatarUrl)
            .circleCrop()
            .placeholder(ContextCompat.getDrawable(context, R.drawable.ic_baseline_account_circle))
            .into(userAvatar)
    }
}