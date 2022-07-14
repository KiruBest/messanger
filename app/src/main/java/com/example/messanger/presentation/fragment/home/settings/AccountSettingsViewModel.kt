package com.example.messanger.presentation.fragment.home.settings

import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messanger.domain.model.UserDto
import com.example.messanger.domain.repository.IAccountService
import kotlinx.coroutines.launch

class AccountSettingsViewModel(
    private val accountService: IAccountService
) : ViewModel() {
    fun updateUser(userDto: UserDto, bitmap: Bitmap?) {
        viewModelScope.launch {
            accountService.updateUserParams(userDto, bitmap)
        }
    }

    fun getBitmap(data: Intent, contentResolver: ContentResolver) {
        var bitmap: Bitmap? = null

        if (data.hasExtra(DATA)) {
            bitmap = data.extras?.get(DATA) as Bitmap
        }

        data.data?.let { uri ->
            val bmpFactoryOptions = BitmapFactory.Options()

            bitmap = contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream, null, bmpFactoryOptions)
            }

            val orientation = contentResolver.openInputStream(uri)?.use { inputStream ->
                ExifInterface(inputStream).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED
                )
            } ?: ExifInterface.ORIENTATION_UNDEFINED

            bitmap = bitmap?.let { bitmap ->
                rotateImage(bitmap, orientation)
            }
        }
    }

    private fun rotateImage(source: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()

        val angle = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            ExifInterface.ORIENTATION_NORMAL -> 0f
            else -> 0f
        }

        matrix.postRotate(angle)

        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    companion object {
        private const val DATA = "data"
    }
}