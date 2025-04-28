package com.example.setwallpaper.style_zone.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.setwallpaper.R
import com.example.setwallpaper.databinding.FragmentInspireMeBinding
import com.example.setwallpaper.style_zone.activity.AddPersonalStyleActivity
import com.example.setwallpaper.style_zone.style.PersonalStyleAdapter
import com.example.setwallpaper.style_zone.style.PersonalStyleItem
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException

class InspireMeFragment : Fragment(R.layout.fragment_inspire_me) {

    private lateinit var binding: FragmentInspireMeBinding
    private var buttonVisible = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentInspireMeBinding.bind(view)

        binding.inspireMeRecyclerview.layoutManager = LinearLayoutManager(requireContext())

        getStyleData {
                list ->
            requireActivity().runOnUiThread {
                list?.let {
                    val styleList: MutableList<PersonalStyleItem> = ArrayList()
                    styleList.addAll(list)
                    binding.inspireMeRecyclerview.adapter = PersonalStyleAdapter(styleList)
                }
            }
        }

        binding.inspireMeRecyclerview.addOnScrollListener( object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val firstVisible = (binding.inspireMeRecyclerview.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                if (dy > 0 && !buttonVisible && firstVisible >= 5) {
                    showButtonWithTextAnimation(binding.btnAddStyleInspireMe)
                } else if (dy < 0 && buttonVisible) {
                    hideButton(binding.btnAddStyleInspireMe)
                }
            }
        })

        binding.btnAddStyleInspireMe.setOnClickListener {
            startActivity(Intent(requireContext(), AddPersonalStyleActivity::class.java))
        }

    }

    private fun getStyleData(onResult: (List<PersonalStyleItem>?) -> Unit) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://raw.githubusercontent.com/najdimu/Stock/refs/heads/main/style_data.json").build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onResult(null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { json ->
                    println(json)
                    val faqList = Gson().fromJson(json, Array<PersonalStyleItem>::class.java).toList()
                    onResult(faqList)
                } ?: onResult(null)
            }
        })
    }

    private fun showButtonWithTextAnimation(button: Button){
        button.apply {
            visibility = View.VISIBLE

            // SET starting scale to 0x
            scaleX = 0f
            scaleY = 0f

            animate()
                .translationX(-35f)  // Move left
                .translationY(-30f)   // Move up
                .scaleX(1f)           // Increase size from 0x ➔ 1x horizontally
                .scaleY(1f)           // Increase size from 0x ➔ 1x vertically
                .setDuration(500)
                .start()
        }
        buttonVisible = true
    }

    private fun hideButton(button: Button){
        button.apply {
            visibility = View.VISIBLE

            // SET starting scale to 1x
            scaleX = 1f
            scaleY = 1f

            animate()
                .translationX(35f)   // Move right (or wherever you want)
                .translationY(30f)    // Move down
                .scaleX(0f)           // Shrink to 0x horizontally
                .scaleY(0f)           // Shrink to 0x vertically
                .setDuration(500)
                .withEndAction {
                    visibility = View.GONE // Optionally hide after shrink
                }
                .start()
        }
        buttonVisible = false
    }
 }


