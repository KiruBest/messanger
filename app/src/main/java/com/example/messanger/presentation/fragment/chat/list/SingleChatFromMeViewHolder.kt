package com.example.messanger.presentation.fragment.chat.list

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.messanger.databinding.SingleChatMessageLayoutBinding
import com.example.messanger.presentation.model.MessageUi
import com.example.messanger.presentation.utils.asDatHourMinute


class SingleChatFromMeViewHolder(binding: SingleChatMessageLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {
    private val textViewMessage = binding.textViewMessage
    private val time = binding.time
    private val textViewMessageFromCurrentUser = binding.textViewMessageFromCurrentUser
    private val timeCurrentUser = binding.timeCurrentUser
    private val constraintLayoutCompanion = binding.constraintLayoutCompanion
    private val constraintLayoutCurrentUser = binding.constraintLayoutCurrentUser
    private val context = binding.root.context

    fun bind(messageDto: MessageUi, companionId: String) {
        if (messageDto.from == companionId) {
            textViewMessage.text = messageDto.text
            time.text = messageDto.timestamp.asDatHourMinute()
            constraintLayoutCompanion.isVisible = true
            constraintLayoutCurrentUser.isVisible = false
        } else {
            textViewMessageFromCurrentUser.text = messageDto.text
            timeCurrentUser.text = messageDto.timestamp.asDatHourMinute()
            constraintLayoutCurrentUser.isVisible = true
            constraintLayoutCompanion.isVisible = false
        }
    }
}