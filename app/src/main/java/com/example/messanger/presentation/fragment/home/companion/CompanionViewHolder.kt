package com.example.messanger.presentation.fragment.home.companion

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.messanger.R
import com.example.messanger.databinding.CompanionItemLayoutBinding
import com.example.messanger.domain.model.UserDto
import com.example.messanger.presentation.core.CompanionTitleBuilder

class CompanionViewHolder(binding: CompanionItemLayoutBinding): RecyclerView.ViewHolder(binding.root) {
    private val textViewUserName = binding.textViewUserName
    private val imageViewAvatar = binding.imageViewAvatar
    private val context = binding.root.context

    fun bind(userDto: UserDto) {
        textViewUserName.text = CompanionTitleBuilder(userDto, context).getTitle()

        Glide.with(context).load(userDto.avatarUrl)
            .circleCrop()
            .placeholder(ContextCompat.getDrawable(context, R.drawable.ic_baseline_account_circle))
            .into(imageViewAvatar)
    }
}