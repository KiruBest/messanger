package com.example.messanger.presentation.fragment.home.settings

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.messanger.R
import com.example.messanger.databinding.FragmentAccountSettingsBinding
import com.example.messanger.presentation.core.BaseFragment
import com.redmadrobot.inputmask.MaskedTextChangedListener
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class AccountSettings : BaseFragment() {

    private lateinit var binding: FragmentAccountSettingsBinding
    private val viewModel: AccountSettingsViewModel by viewModel()
    private lateinit var pictureActivityResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pictureActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    data?.let { intent ->
                        viewModel.getBitmap(
                            intent,
                            requireContext().contentResolver
                        )
                    }
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editTextPhone.addTextChangedListener(
            MaskedTextChangedListener(
                "+7 ([000]) [000]-[00]-[00]",
                binding.editTextPhone
            )
        )

        val cal = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLable(cal)
        }

        binding.editTextDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun updateLable(cal: Calendar) {
        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
            binding.editTextDate.setText(sdf.format(cal.time))

        //TODO -> Запизать эту штуку в кнопку и удалить лишнее. Потом с помощью Glide загрузить картинку в imageView
//        MaterialAlertDialogBuilder(requireContext(), R.style.DialogAvatarPicker)
//            .setTitle(getString(R.string.change_avatar_title))
//            .setItems(
//                arrayOf(
//                    getString(R.string.make_photo),
//                    getString(R.string.pick_photo_from_gallery),
//                    deletePhotoSpannableText
//                )
//            ) { _, which ->
//                when (which) {
//                    ACTION_OPEN_CAMERA -> {
//                        val makePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                        pictureActivityResult.launch(makePicture)
//                        deleteAvatarStatus = false
//                    }
//                    ACTION_OPEN_GALLERY -> {
//                        val takePicture = Intent(Intent.ACTION_PICK)
//                        takePicture.type = "image/*"
//                        pictureActivityResult.launch(takePicture)
//                        deleteAvatarStatus = false
//                    }
//                    ACTION_DELETE_AVATAR -> {
//                        deleteAvatarStatus = true
//                        viewBinding.imageViewAvatar.setImageDrawable(
//                            ContextCompat.getDrawable(
//                                requireContext(), R.drawable.ic_baseline_account_circle
//                            )
//                        )
//                    }
//                }
//            }
//            .show()
    }
}