package com.example.messanger.presentation.fragment.home.companion

import androidx.recyclerview.widget.DiffUtil
import com.example.messanger.presentation.model.UserUi

class CompanionDiffUtils(
    private val oldUsersList: List<UserUi>,
    private val newUsersList: List<UserUi>
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