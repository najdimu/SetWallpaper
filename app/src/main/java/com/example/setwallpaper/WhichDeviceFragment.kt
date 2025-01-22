package com.example.setwallpaper

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.setwallpaper.databinding.FragmentWhichDeviceBinding

class WhichDeviceFragment : Fragment(R.layout.fragment_which_device) {

    private lateinit var binding: FragmentWhichDeviceBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWhichDeviceBinding.bind(view)

        binding.backBtnWhichFg.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.containerFg, MainFragment())
                .commit()
        }

        binding.samsungLayout.setOnClickListener {
            val targetFragment = AddThemeFragment()
            targetFragment.arguments = Bundle().apply { putString("device","Samsung Galaxy theme") }
            parentFragmentManager.beginTransaction()
                .replace(R.id.containerFg, targetFragment)
                .addToBackStack(null)
                .commit()
        }
        binding.oppoRealmeLayout.setOnClickListener {
            val targetFragment = AddThemeFragment()
            targetFragment.arguments = Bundle().apply { putString("device","Oppo & Realme theme") }
            parentFragmentManager.beginTransaction()
                .replace(R.id.containerFg, targetFragment)
                .addToBackStack(null)
                .commit()
        }
        binding.otherDeviceLayout.setOnClickListener {
            val targetFragment = AddThemeFragment()
            targetFragment.arguments = Bundle().apply { putString("device","Other device's theme") }
            parentFragmentManager.beginTransaction()
                .replace(R.id.containerFg, targetFragment)
                .addToBackStack(null)
                .commit()
        }

    }


}