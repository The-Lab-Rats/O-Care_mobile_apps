package com.example.mainapplabrats.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.mainapplabrats.R


class SplashFragment : Fragment() {
    private val TAG : String = "CHECK_RESPONE"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Handler().postDelayed({
            if(onBoardingFinished()){
                Log.d(TAG, "MASUK SCOP SPLASH IF")
                findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
            }else{
                Log.d(TAG, "MASUK SCOP SPLASH ELSE")
                findNavController().navigate(R.id.action_splashFragment_to_viewPagerFragment)
            }
        }, 2000)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    private fun onBoardingFinished(): Boolean{
        Log.d(TAG, "MASUK SCOP SPLASH BOOLEAN")
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished", false)
    }

}