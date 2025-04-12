package com.example.setwallpaper.style_zone.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.setwallpaper.R
import com.example.setwallpaper.databinding.FragmentInspireMeBinding
import com.example.setwallpaper.style_zone.activity.AddPersonalStyleActivity
import com.example.setwallpaper.style_zone.style.PersonalStyleAdapter
import com.example.setwallpaper.style_zone.style.PersonalStyleItem
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException

class InspireMeFragment : Fragment(R.layout.fragment_inspire_me) {

    private lateinit var binding: FragmentInspireMeBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentInspireMeBinding.bind(view)

        binding.inspireMeRecyclerview.layoutManager = LinearLayoutManager(requireContext())

        getFAQData {
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

                    val firstItemPos =
                        (binding.inspireMeRecyclerview.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                    if (firstItemPos >= 4) {
                        if (binding.btnAddStyleInspireMe.visibility != View.VISIBLE) {
                            // binding.btnAddStyleInspireMe.animate().translationX(0f).setDuration(300).start()
                            binding.btnAddStyleInspireMe.animate()
                                .translationX(-100f)  // Move to the center horizontally
                                .translationY(-50f)  // Move to the center vertically
                                .scaleX(2f)        // Increase size horizontally (from 1x to 2x)
                                .scaleY(2f)        // Increase size vertically (from 1x to 2x)
                                .setDuration(500)
                                .start()
                            binding.btnAddStyleInspireMe.visibility = View.VISIBLE
                        }

                }
            }
        })

        binding.btnAddStyleInspireMe.setOnClickListener {
            startActivity(Intent(requireContext(), AddPersonalStyleActivity::class.java))
        }

    }

    private fun getFAQData(onResult: (List<PersonalStyleItem>?) -> Unit) {
        val client = OkHttpClient()
        val request = Request.Builder().url("https://raw.githubusercontent.com/najdimu/Stock/refs/heads/main/style_data.json").build()

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


    }

