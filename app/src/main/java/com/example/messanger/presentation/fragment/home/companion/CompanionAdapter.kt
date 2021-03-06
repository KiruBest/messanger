package com.example.messanger.presentation.fragment.home.companion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.messanger.databinding.CompanionItemLayoutBinding
import com.example.messanger.domain.model.UserDto

class CompanionAdapter(
    private var usersList: List<UserDto>,
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

    fun updateData(newUsersList: List<UserDto>) {
        val companionDiffUtils = CompanionDiffUtils(usersList, newUsersList)
        val diffResult = DiffUtil.calculateDiff(companionDiffUtils)
        usersList = newUsersList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onClick(v: View?) {
        onItemClickListener.invoke(v?.tag as UserDto)
    }
}