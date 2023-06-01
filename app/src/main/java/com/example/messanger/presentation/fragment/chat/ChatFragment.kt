package com.example.messanger.presentation.fragment.chat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.messanger.R
import com.example.messanger.core.constants.Constants
import com.example.messanger.core.enumeration.CallState
import com.example.messanger.core.enumeration.KEY_ARG_CALL_STATE
import com.example.messanger.core.result.OperationResult
import com.example.messanger.databinding.FragmentChatBinding
import com.example.messanger.presentation.fragment.base.BaseFragment
import com.example.messanger.presentation.fragment.chat.list.SingleChatAdapter
import com.example.messanger.presentation.fragment.home.settings.AccountSettings
import com.example.messanger.presentation.fragment.home.settings.AccountSettingsViewModel
import com.example.messanger.presentation.model.UserUi
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.lang.invoke.CallSite

class ChatFragment : BaseFragment<ChatViewModel, FragmentChatBinding>(
    viewBindingInflater = FragmentChatBinding::inflate,
    layoutId = R.layout.fragment_chat,
) {
    override val viewModel: ChatViewModel by viewModel {
        parametersOf(requireArguments().get(Constants.COMPANION_ID))
    }

    private val singleChatAdapter by lazy {
        SingleChatAdapter(viewModel.companionID)
    }

    private lateinit var pictureActivityResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pictureActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    data?.let { intent ->
                        viewModel.sendPicture(
                            intent,
                            requireContext().contentResolver
                        )
                    }
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageButtonCall.setOnClickListener {
            navigateToCallFragment(CallState.AUDIO)
        }

        binding.imageButtonVideoCall.setOnClickListener {
            navigateToCallFragment(CallState.VIDEO)
        }

        binding.imageViewAttachFile.setOnClickListener {
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
                        AccountSettings.ACTION_OPEN_CAMERA -> {
                            val makePicture =
                                Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            pictureActivityResult.launch(makePicture)
                        }
                        AccountSettings.ACTION_OPEN_GALLERY -> {
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

        binding.apply {
            navIcon.setOnClickListener {
                findNavController().popBackStack(R.id.chatFragment, true)
            }

            recyclerViewMessages.apply {
                val linearLayoutManager = LinearLayoutManager(requireContext()).apply {
                    reverseLayout = false
                }
                adapter = singleChatAdapter
                layoutManager = linearLayoutManager

                addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
                    if (linearLayoutManager.findLastVisibleItemPosition() != singleChatAdapter.itemCount - 1) {
                        recyclerViewMessages.smoothScrollBy(0, oldBottom - bottom)
                    }
                }

                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        if (dy < 0) {
                            val v = requireActivity().currentFocus
                            val imm = requireActivity().getSystemService(
                                Context.INPUT_METHOD_SERVICE
                            ) as InputMethodManager
                            imm.hideSoftInputFromWindow(v?.windowToken, 0)
                        }
                    }
                })
            }

            imageViewSendMessage.setOnClickListener {
                val message = editTextSendMessage.text.toString()
                viewModel.sendMessage(message)
                editTextSendMessage.text.clear()
                recyclerViewMessages.scrollToPosition(singleChatAdapter.itemCount - 1)
            }

            lifecycleScope.launchWhenCreated {
                viewModel.messageListFlow.collect { result ->
                    when (result) {
                        is OperationResult.Empty -> {}
                        is OperationResult.Error -> TODO()
                        is OperationResult.Loading -> {}
                        is OperationResult.Success -> {
                            val messages = result.data
                            singleChatAdapter.updateMessageList(messages)
                            recyclerViewMessages.scrollToPosition(singleChatAdapter.itemCount - 1)
                            messages.forEach {
                                if (!it.seen) viewModel.readMessage(it.id)
                            }
                        }
                    }
                }
            }

            lifecycleScope.launchWhenCreated {
                viewModel.companionFlow.collect { result ->
                    when (result) {
                        is OperationResult.Empty -> {}
                        is OperationResult.Error -> {}
                        is OperationResult.Loading -> {}
                        is OperationResult.Success -> {
                            val companion = result.data
                            toolbar.findViewById<TextView>(R.id.companionName).text =
                                companion.fullName

                            toolbar.findViewById<TextView>(R.id.companionStatus).text =
                                companion.status.status

                            Glide.with(requireContext())
                                .load(companion.avatarUrl)
                                .circleCrop()
                                .placeholder(R.drawable.ic_baseline_account_circle)
                                .into(toolbar.findViewById(R.id.companionAvatar))
                        }
                    }
                }
            }
        }
    }

    private fun navigateToCallFragment(callState: CallState) {
        val bundle = bundleOf(Constants.USER_DTO to viewModel.getUser(), KEY_ARG_CALL_STATE to callState)
        findNavController().navigate(R.id.action_chatFragment_to_callFragment, bundle)
    }
}