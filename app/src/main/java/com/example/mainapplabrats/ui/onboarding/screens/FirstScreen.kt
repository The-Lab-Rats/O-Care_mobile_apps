package com.example.mainapplabrats.ui.onboarding.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.mainapplabrats.R
import com.example.mainapplabrats.databinding.FragmentFirstScreenBinding

class FirstScreen : Fragment() {
    private lateinit var binding: FragmentFirstScreenBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFirstScreenBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val viewPager =  activity?.findViewById<ViewPager2>(R.id.viewPager)

        binding.next.setOnClickListener {
            viewPager?.currentItem = 1
        }

        return root
    }

}