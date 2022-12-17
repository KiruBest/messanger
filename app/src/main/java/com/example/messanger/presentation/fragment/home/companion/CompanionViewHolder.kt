package com.example.messanger.presentation.fragment.home.companion

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.messanger.R
import com.example.messanger.databinding.CompanionItemLayoutBinding
import com.example.messanger.presentation.model.UserUi

class CompanionViewHolder(binding: CompanionItemLayoutBinding): RecyclerView.ViewHolder(binding.root) {
    private val textViewUserName = binding.textViewUserName
    private val imageViewAvatar = binding.imageViewAvatar
    private val context = binding.root.context

    fun bind(userDto: UserUi) {
        textViewUserName.text = userDto.fullName

        Glide.with(context).load(userDto.avatarUrl)
            .circleCrop()
            .placeholder(ContextCompat.getDrawable(context, R.drawable.ic_baseline_account_circle))
            .into(imageViewAvatar)
    }
}