package com.example.setwallpaper

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.setwallpaper.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_background)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPreferences = getSharedPreferences("tasbeh_models", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        count = sharedPreferences.getInt("count_number", 0)
        binding.counterNum.text = "$count"


        binding.btnCounter.setOnClickListener {
            count = sharedPreferences.getInt("count_number", 0)
            count++
            binding.counterNum.text = "$count"
            editor.putInt("count_number", count)
            editor.apply()
        }
        binding.btnReset.setOnClickListener {
            count = 0
            binding.counterNum.text = "$count"
            editor.putInt("count_number", 0)
            editor.apply()
        }

        binding.btnLed.setOnClickListener {
            val isLed = sharedPreferences.getBoolean("is_led", false)
            if (!isLed) {
                binding.mainBackground.setBackgroundResource(R.drawable.background_led)
                binding.imageBackground.setBackgroundResource(R.drawable.ic_back_full_led)
                binding.btnReset.setBackgroundResource(R.drawable.ic_button_led)
                binding.btnCounter.setBackgroundResource(R.drawable.ic_button_led)
                binding.btnLed.setBackgroundResource(R.drawable.ic_button_led)
                editor.putBoolean("is_led", true)
                editor.apply()
            } else {
                binding.mainBackground.setBackgroundResource(R.drawable.background)
                binding.imageBackground.setBackgroundResource(R.drawable.ic_back_full)
                binding.btnReset.setBackgroundResource(R.drawable.ic_button)
                binding.btnCounter.setBackgroundResource(R.drawable.ic_button)
                binding.btnLed.setBackgroundResource(R.drawable.ic_button)
                editor.putBoolean("is_led", false)
                editor.apply()
            }


        }

    }
}

