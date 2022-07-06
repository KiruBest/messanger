package com.example.messanger.presentation.fragment.chat

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.messanger.databinding.SingleChatMessageLayoutBinding
import com.example.messanger.domain.model.MessageDto
import com.example.messanger.presentation.core.asDatHourMinute

class SingleChatViewHolder(binding: SingleChatMessageLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {
    private val textViewMessage = binding.textViewMessage
    private val time = binding.time
    private val textViewMessageFromCurrentUser = binding.textViewMessageFromCurrentUser
    private val timeCurrentUser = binding.timeCurrentUser
    private val constraintLayoutCompanion = binding.constraintLayoutCompanion
    private val constraintLayoutCurrentUser = binding.constraintLayoutCurrentUser
    private val context = binding.root.context

    fun bind(messageDto: MessageDto, companionId: String) {


        if (messageDto.from == companionId) {
            textViewMessage.text = messageDto.text
            time.text = messageDto.timestamp.asDatHourMinute()
            constraintLayoutCompanion.visibility = View.VISIBLE
            constraintLayoutCurrentUser.visibility = View.GONE
        } else {
            textViewMessageFromCurrentUser.text = messageDto.text
            timeCurrentUser.text = messageDto.timestamp.asDatHourMinute()
            constraintLayoutCurrentUser.visibility = View.VISIBLE
            constraintLayoutCompanion.visibility = View.GONE
        }
    }
}