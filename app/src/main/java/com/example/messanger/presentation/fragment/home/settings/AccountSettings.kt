package com.example.messanger.presentation.fragment.home.settings

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.icu.util.Calendar
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.messanger.R
import com.example.messanger.core.result.OperationResult
import com.example.messanger.databinding.FragmentAccountSettingsBinding
import com.example.messanger.presentation.fragment.base.BaseFragment
import com.example.messanger.presentation.model.UserUi
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.redmadrobot.inputmask.MaskedTextChangedListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class AccountSettings : BaseFragment<AccountSettingsViewModel, FragmentAccountSettingsBinding>(
    layoutId = R.layout.fragment_account_settings,
    viewBindingInflater = FragmentAccountSettingsBinding::inflate
) {

    private lateinit var pictureActivityResult: ActivityResultLauncher<Intent>
    private var bitmap: Bitmap? = null
    override val viewModel: AccountSettingsViewModel by viewModel()
    private var user: UserUi? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pictureActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    data?.let { intent ->
                        bitmap = viewModel.getBitmap(
                            intent,
                            requireContext().contentResolver
                        )
                    }
                }
            }
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
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
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

        binding.toolbarAccount.setNavigationOnClickListener {
            findNavController().popBackStack(R.id.accountSettings, true)
        }


        viewModel.getCurrentUser()
        lifecycleScope.launchWhenCreated {
            viewModel.userDtoFlow.collect {
                /* todo (в целом полная фигня получилась с таким подходом, можно было сделать проще
                надо обрабатывать OperationResult во вьюмодели и отдавать готовое флоу(или LiveData)) */
                when (it) {
                    is OperationResult.Empty -> {

                    }
                    is OperationResult.Error -> {

                    }
                    is OperationResult.Loading -> {

                    }
                    is OperationResult.Success -> {
                        user = it.data
                        Log.d("Tri", it.data.toString())
                        user?.let { userDto ->
                            binding.editTextFirstName.setText(userDto.fName)
                            binding.editTextLastName.setText(userDto.lName)
                            binding.editTextMiddleName.setText(userDto.mName)
                            binding.editTextDate.setText(userDto.dataBirth)
                            binding.editTextPhone.setText(userDto.phone)

                            Glide.with(requireContext()).load(userDto.avatarUrl)
                                .placeholder(R.drawable.ic_baseline_account_circle)
                                .circleCrop().into(binding.imageViewAccount)
                        }

                        binding.buttonSave.setOnClickListener {
                            user?.let { userDto ->
                                val newUser = userDto.copy(
                                    fName = binding.editTextFirstName.text.toString(),
                                    lName = binding.editTextLastName.text.toString(),
                                    mName = binding.editTextMiddleName.text.toString(),
                                    dataBirth = binding.editTextDate.text.toString(),
                                    phone = binding.editTextPhone.text.toString(),
                                )
                                viewModel.updateUser(newUser, bitmap)
                            }
                        }


                        lifecycleScope.launchWhenCreated {
                            viewModel.isSuccessFlow.collect{
                                if (it != null ){
                                    Toast.makeText(requireContext(),"Успешно!",Toast.LENGTH_SHORT).show()
                                    findNavController().popBackStack(R.id.accountSettings, true)
                                }
                            }
                        }

                        binding.buttonChangePhoto.setOnClickListener {
                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Сменить фото")
                                .setItems(
                                    arrayOf(
                                        "Сделать фото",
                                        "Выбрать из галереи",
                                        "Выход"
                                    )
                                ) { _, which ->
                                    when (which) {
                                        ACTION_OPEN_CAMERA -> {
                                            val makePicture =
                                                Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                            pictureActivityResult.launch(makePicture)
                                        }
                                        ACTION_OPEN_GALLERY -> {
                                            val takePicture = Intent(Intent.ACTION_PICK)
                                            takePicture.type = "image/*"
                                            pictureActivityResult.launch(takePicture)
                                        }
//                        ACTION_DELETE_AVATAR -> {
//                            viewBinding.imageViewAvatar.setImageDrawable(
//                                ContextCompat.getDrawable(
//                                    requireContext(), R.drawable.ic_baseline_account_circle
//                                )
//                            )
//                        }
                                    }
                                }
                                .show()
                        }
                    }
                }
            }
        }
    }

    private fun updateLable(cal: Calendar) {
        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding.editTextDate.setText(sdf.format(cal.time))
    }

    override fun onResume() {
        super.onResume()

        bitmap?.let {
            Glide.with(requireContext()).load(bitmap)
                .placeholder(R.drawable.ic_baseline_account_circle)
                .circleCrop().into(binding.imageViewAccount)
        }
    }

    companion object {
        private const val ACTION_OPEN_CAMERA = 0
        private const val ACTION_OPEN_GALLERY = 1
        private const val ACTION_DELETE_AVATAR = 2
    }
}