package com.example.messanger.presentation.fragment.home.settings

import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messanger.core.extensions.mapIfSuccess
import com.example.messanger.core.result.OperationResult
import com.example.messanger.domain.repository.IAccountService
import com.example.messanger.presentation.model.UserUi
import com.example.messanger.presentation.model.mapToDomain
import com.example.messanger.presentation.model.mapToUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AccountSettingsViewModel(
    private val accountService: IAccountService
) : ViewModel() {
    private val _userDtoFlow =
        MutableStateFlow<OperationResult<UserUi>>(OperationResult.Loading)
    val userDtoFlow: StateFlow<OperationResult<UserUi>> = _userDtoFlow.asStateFlow()

    private val _isSuccessFlow: MutableStateFlow<Unit?> = MutableStateFlow(null)
    val isSuccessFlow: StateFlow<Unit?> = _isSuccessFlow.asStateFlow()

    fun updateUser(userDto: UserUi, bitmap: Bitmap?) {
        viewModelScope.launch {
            accountService.updateUserParams(userDto.mapToDomain(), bitmap)
        }
        _isSuccessFlow.tryEmit(Unit)
    }

    fun getBitmap(data: Intent, contentResolver: ContentResolver): Bitmap? {
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
        return bitmap
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

    fun getCurrentUser() {
        viewModelScope.launch {
            val result = accountService.getCurrentUser().mapIfSuccess {
                OperationResult.Success(it.mapToUi())
            }
            _userDtoFlow.tryEmit(result)
        }
    }

    fun setUserAccountStatus(text: String) {
        viewModelScope.launch {
            accountService.setUserAccountStatus(text)
        }
    }

    companion object {
        private const val DATA = "data"
    }
}