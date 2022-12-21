package com.example.messanger.presentation.fragment.home.companion

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.messanger.databinding.CompanionItemLayoutBinding
import com.example.messanger.presentation.model.UserUi

class CompanionAdapter(
    private val onItemClickListener: (userUi: UserUi) -> Unit
) : RecyclerView.Adapter<CompanionViewHolder>() {

    private var usersList: List<UserUi> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CompanionItemLayoutBinding.inflate(inflater, parent, false)
        return CompanionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CompanionViewHolder, position: Int) {
        holder.bind(usersList[position], onItemClickListener)
        holder.itemView.tag = usersList[position]
    }

    override fun getItemCount(): Int = usersList.size

    fun updateData(newUsersList: List<UserUi>) {
        val companionDiffUtils = CompanionDiffUtils(usersList, newUsersList)
        val diffResult = DiffUtil.calculateDiff(companionDiffUtils)
        usersList = newUsersList
        diffResult.dispatchUpdatesTo(this)
    }
}