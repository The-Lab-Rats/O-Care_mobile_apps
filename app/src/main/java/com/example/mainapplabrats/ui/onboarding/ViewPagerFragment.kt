package com.example.mainapplabrats.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mainapplabrats.adapter.ViewPagerAdapter
import com.example.mainapplabrats.databinding.FragmentViewPagerBinding
import com.example.mainapplabrats.ui.onboarding.screens.FirstScreen
import com.example.mainapplabrats.ui.onboarding.screens.SecondScreen
import com.example.mainapplabrats.ui.onboarding.screens.ThirdScreen

class ViewPagerFragment : Fragment() {
    private lateinit var binding : FragmentViewPagerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentViewPagerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val fragmentList = arrayListOf<Fragment>(
            FirstScreen(),
            SecondScreen(),
            ThirdScreen()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        binding.viewPager.adapter = adapter

        return root
    }

}