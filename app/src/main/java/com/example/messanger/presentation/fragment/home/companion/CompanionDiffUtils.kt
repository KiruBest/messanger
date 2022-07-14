package com.example.messanger.presentation.fragment.home.companion

import androidx.recyclerview.widget.DiffUtil
import com.example.messanger.domain.model.UserDto

class CompanionDiffUtils(
    private val oldUsersList: List<UserDto>,
    private val newUsersList: List<UserDto>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldUsersList.size

    override fun getNewListSize(): Int = newUsersList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldUser = oldUsersList[oldItemPosition]
        val newUser = newUsersList[newItemPosition]
        return oldUser.id == newUser.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldUser = oldUsersList[oldItemPosition]
        val newUser = newUsersList[newItemPosition]
        return oldUser == newUser
    }
}