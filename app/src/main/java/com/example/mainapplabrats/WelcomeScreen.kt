package com.example.mainapplabrats

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mainapplabrats.databinding.WelcomeScreenBinding

class WelcomeScreen : AppCompatActivity() {

    private lateinit var binding: WelcomeScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WelcomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
    }
}