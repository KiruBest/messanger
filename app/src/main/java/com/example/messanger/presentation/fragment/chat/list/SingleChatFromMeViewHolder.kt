package com.example.messanger.presentation.fragment.chat.list

import com.example.messanger.databinding.ItemSingleChatMessageMeBinding
import com.example.messanger.presentation.model.MessageUi
import com.example.messanger.presentation.utils.asDatHourMinute

class SingleChatFromMeViewHolder(private val binding: ItemSingleChatMessageMeBinding) :
    MessageViewHolder(binding.root) {

    override fun bind(messageDto: MessageUi) {
        binding.apply {
            textViewMessageFromCurrentUser.text = messageDto.text
            timeCurrentUser.text = messageDto.timestamp.asDatHourMinute()
            messageDto.loadImageIfPictureMessage(context = root.context, imgView = imgMessage)
        }
    }
}