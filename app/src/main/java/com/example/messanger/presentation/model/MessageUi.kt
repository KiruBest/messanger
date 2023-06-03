package com.example.messanger.presentation.model

import android.content.Context
import android.widget.ImageView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.messanger.R
import com.example.messanger.core.enumeration.MessageType

data class MessageUi(
    val id: String,
    val text: String,
    val type: String,
    val from: String,
    val timestamp: Long,
    val seen: Boolean
) {
    val isPictureMessage: Boolean = type == MessageType.IMAGE.type

    fun isCompanion(companionId: String) = from == companionId

    fun loadImageIfPictureMessage(context: Context, imgView: ImageView) {

        imgView.isVisible = isPictureMessage
        if (isPictureMessage) {
            Glide.with(context).load(text)
                .placeholder(R.drawable.ic_baseline_account_circle)
                .into(imgView)
        }
    }
}