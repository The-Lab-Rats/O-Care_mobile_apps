package com.example.mainapplabrats.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.mainapplabrats.databinding.FragmentOperationsBinding

class OperationsFragment : Fragment() {

    private var _binding: FragmentOperationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentOperationsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setToolbar()
        return root
    }
    private fun setToolbar() {
        binding.toolbar.tvToolbarTitle.text = "Cara Kerja"
        binding.toolbar.btnBackToolbar.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}