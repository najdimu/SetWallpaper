package com.example.setwallpaper.publish

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.setwallpaper.R
import com.example.setwallpaper.databinding.FragmentMainBinding


class MainFragment : Fragment(R.layout.fragment_main) {

    private lateinit var binding: FragmentMainBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMainBinding.bind(view)

        binding.backBtnMainFg.setOnClickListener {
            requireActivity().finish()
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