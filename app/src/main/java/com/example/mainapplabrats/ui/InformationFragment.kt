package com.example.mainapplabrats.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mainapplabrats.R
import com.example.mainapplabrats.databinding.FragmentInformationBinding

class InformationFragment : Fragment() {

    private var _binding: FragmentInformationBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var btnOps : Button
    private lateinit var btnRemind : Button
    private lateinit var btnAbout : Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentInformationBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setToolbar()
        btnOps = binding.buttonPenggunaan
        btnRemind = binding.buttonNotifikasi
        btnAbout =  binding.buttonTentangAplikasi

        btnAbout.setOnClickListener {
            findNavController().navigate(R.id.navigation_about)
        }
        btnOps.setOnClickListener {
           findNavController().navigate(R.id.navigation_operation)
        }
        btnRemind.setOnClickListener {
            findNavController().navigate(R.id.navigation_reminder)
        }
        binding.buttonDisclaimer.setOnClickListener {
            findNavController().navigate(R.id.navigation_disclaimer)
        }
        return root
    }
    private fun setToolbar() {
        binding.toolbar.tvToolbarTitle.text = "Tentang Aplikasi"
        binding.toolbar.btnBackToolbar.visibility = View.GONE
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}