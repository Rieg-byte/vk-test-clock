package com.example.vktestclock

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.vktestclock.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private var f: Boolean = false
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding for ActivityMainBinding must not be null")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.button.setOnClickListener {
            f = if (f) {
                binding.customClockView1.setNumberColor(Color.BLUE)
                false
            } else {
                binding.customClockView1.setNumberColor(Color.GREEN)
                true
            }
        }
    }
}