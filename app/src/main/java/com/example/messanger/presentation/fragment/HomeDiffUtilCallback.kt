package com.example.messanger.presentation.fragment

import androidx.recyclerview.widget.DiffUtil
import com.example.messanger.domain.model.ChatItemDto

class HomeDiffUtilCallback(
    private val oldList: List<ChatItemDto>,
    private val newList: List<ChatItemDto>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.userID == newItem.userID
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem == newItem
    }
}