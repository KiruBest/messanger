package com.example.messanger.presentation.fragment.chat.list

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.messanger.presentation.model.MessageUi

abstract class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(messageDto: MessageUi)
}