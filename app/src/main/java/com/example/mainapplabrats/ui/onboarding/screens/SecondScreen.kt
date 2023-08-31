package com.example.mainapplabrats.ui.onboarding.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.mainapplabrats.R
import com.example.mainapplabrats.databinding.FragmentSecondScreenBinding

class SecondScreen : Fragment() {
    private lateinit var binding : FragmentSecondScreenBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSecondScreenBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val viewPager =  activity?.findViewById<ViewPager2>(R.id.viewPager)

        binding.next2.setOnClickListener {
            viewPager?.currentItem = 2
        }

        return root
    }

}