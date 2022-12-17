package com.example.messanger.presentation.fragment.chat

import androidx.recyclerview.widget.DiffUtil
import com.example.messanger.presentation.model.MessageUi

class SingleChatDiffUtil(
    private val oldList: List<MessageUi>,
    private val newList: List<MessageUi>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem == newItem
    }
}