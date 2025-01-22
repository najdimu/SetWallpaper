package com.example.setwallpaper

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.setwallpaper.databinding.FragmentMainBinding


class MainFragment : Fragment(R.layout.fragment_main) {

    private lateinit var binding: FragmentMainBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMainBinding.bind(view)

        binding.backBtnMainFg.setOnClickListener {
            TODO("Intent koymali basganda intent bilan bashdaki yera kaytmaly")
        }

        binding.wallpaperLayout.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.containerFg, AddWallpaperFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.themeLayout.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.containerFg, WhichDeviceFragment())
                .addToBackStack(null)
                .commit()
        }


    }


}