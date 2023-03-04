package com.example.messanger.presentation.fragment.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

abstract class BaseFragment<ViewModel : BaseViewModel, ViewBinding : androidx.viewbinding.ViewBinding>(
    private val viewBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding,
    @LayoutRes layoutId: Int
) : Fragment(layoutId) {
    private var _binding: ViewBinding? = null
    protected val binding get() = requireNotNull(_binding)
    protected abstract val viewModel: ViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = viewBindingInflater(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}