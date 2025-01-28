package com.example.setwallpaper

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.setwallpaper.databinding.FragmentResponseBinding


class ResponseFragment : Fragment(R.layout.fragment_response) {

    private lateinit var binding: FragmentResponseBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentResponseBinding.bind(view)

        binding.backBtnResponseFg.setOnClickListener { requireActivity().finish() }

        binding.btnDoneResponseFg.setOnClickListener { requireActivity().finish() }

        val toolbarTitle = arguments?.getBoolean("message_result")
        if (toolbarTitle == true){
            binding.toolbarTitleResponse.text = getString(R.string.publish_theme_toolbar)
            binding.textViewResponseFg.text = getString(R.string.success_response_message_theme)
        } else{
            binding.toolbarTitleResponse.text = getString(R.string.publish_wallpaper)
            binding.textViewResponseFg.text = getString(R.string.success_response_message_wallpaper)
        }

    }


}