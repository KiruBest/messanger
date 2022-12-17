package com.example.messanger.presentation.fragment.home.companion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.messanger.data.model.UserDto
import com.example.messanger.databinding.CompanionItemLayoutBinding
import com.example.messanger.presentation.model.UserUi

class CompanionAdapter(
    private var usersList: List<UserUi>,
    private val onItemClickListener: (userDto: UserDto) -> Unit
): RecyclerView.Adapter<CompanionViewHolder>(), View.OnClickListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CompanionItemLayoutBinding.inflate(inflater, parent, false)

        binding.root.setOnClickListener(this)

        return CompanionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CompanionViewHolder, position: Int) {
        holder.bind(usersList[position])
        holder.itemView.tag = usersList[position]
    }

    override fun getItemCount(): Int = usersList.size

    fun updateData(newUsersList: List<UserUi>) {
        val companionDiffUtils = CompanionDiffUtils(usersList, newUsersList)
        val diffResult = DiffUtil.calculateDiff(companionDiffUtils)
        usersList = newUsersList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onClick(v: View?) {
        onItemClickListener.invoke(v?.tag as UserDto)
    }
}