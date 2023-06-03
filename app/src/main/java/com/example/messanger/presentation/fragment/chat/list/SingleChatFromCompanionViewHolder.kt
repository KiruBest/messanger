package com.example.messanger.presentation.fragment.chat.list

import com.example.messanger.databinding.ItemSingleChatMessageCompanionBinding
import com.example.messanger.presentation.model.MessageUi
import com.example.messanger.presentation.utils.asDatHourMinute

class SingleChatFromCompanionViewHolder(private val binding: ItemSingleChatMessageCompanionBinding) :
    MessageViewHolder(binding.root) {

    override fun bind(messageDto: MessageUi) {
        binding.apply {
            textViewMessage.text = messageDto.text
            time.text = messageDto.timestamp.asDatHourMinute()
            messageDto.loadImageIfPictureMessage(context = root.context, imgView = imgMessage)
        }
    }
}