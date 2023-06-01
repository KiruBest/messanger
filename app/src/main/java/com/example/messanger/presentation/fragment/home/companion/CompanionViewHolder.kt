package com.example.messanger.presentation.fragment.home.companion

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.messanger.R
import com.example.messanger.databinding.CompanionItemLayoutBinding
import com.example.messanger.presentation.model.UserUi

class CompanionViewHolder(private val binding: CompanionItemLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(userDto: UserUi, onItemClickListener: (user: UserUi) -> Unit) {
        binding.apply {
            root.setOnClickListener {
                onItemClickListener.invoke(userDto)
            }
            textViewUserName.text = userDto.fullName

            Glide.with(root.context).load(userDto.avatarUrl)
                .circleCrop()
                .placeholder(
                    ContextCompat.getDrawable(
                        root.context,
                        R.drawable.ic_baseline_account_circle
                    )
                )
                .into(imageViewAvatar)
        }
    }
}