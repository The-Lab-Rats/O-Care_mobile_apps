package com.example.mainapplabrats.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.mainapplabrats.databinding.FragmentAboutBinding

class NotificationsFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setToolbar()
        return root
    }
    private fun setToolbar() {
        binding.toolbar.tvToolbarTitle.text = "Tentang Aplikasi"
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}