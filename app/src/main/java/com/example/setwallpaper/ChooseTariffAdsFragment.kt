package com.example.setwallpaper

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.setwallpaper.databinding.FragmentChooseTariffAdsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import org.json.JSONObject

class ChooseTariffAdsFragment : Fragment(R.layout.fragment_choose_tariff_ads) {

    private lateinit var binding: FragmentChooseTariffAdsBinding

    private var tariffID = 1


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChooseTariffAdsBinding.bind(view)

        binding.backBtnChooseTariffFg.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.containerFg, GetCodeFragment())
                .commit()
        }

        binding.layoutTariffWeek.setOnClickListener { checkTariff(0) }
        binding.layoutTariffMonth.setOnClickListener { checkTariff(1) }
        binding.layoutTariff3Month.setOnClickListener { checkTariff(2) }
        binding.layoutTariff6Month.setOnClickListener { checkTariff(3) }
        binding.layoutTariffYear.setOnClickListener { checkTariff(4) }

        binding.textCheckboxConditionsLink.setOnClickListener {
            // TODO uslovya link koymaly
        }



        binding.btnBuy.setOnClickListener {
            val userEmail = binding.edittextEmail.text.toString()
            val userName = binding.edittextName.text.toString()
            val checkBox = binding.checkboxTerms.isChecked

            if (userEmail.isEmpty() || !checkBox) {
                if (userEmail.isEmpty()){
                    binding.textWhenUnfillEmail.visibility = View.VISIBLE
                    binding.textCheckboxConditionsMessage.visibility = View.GONE
                }
                else{
                    binding.textWhenUnfillEmail.visibility = View.GONE
                    binding.textCheckboxConditionsMessage.visibility = View.VISIBLE
                }
            }
            else {
                binding.textWhenUnfillEmail.visibility = View.GONE
                binding.textCheckboxConditionsMessage.visibility = View.GONE
                try{
                    val jsonObject = addTextsToJsonObject(tariffID, userEmail, userName)
                    sendToServer(jsonObject, "https://yourserver.com/api/upload/theme")
                    println(jsonObject)

                    val sharedPreferences = requireContext().getSharedPreferences("remove_tariff", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putInt("tariff_ID", tariffID)
                    editor.putBoolean("tariff_status", true)
                    editor.apply()

                    //TODO shora yomoney web koysangyz boldi
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.containerFg, GetCodeFragment())
                        .commit()

                }catch  (e: Exception) {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        }
    }


    private fun checkTariff(layout: Int){
        when (layout){
            0 ->{
                tariffID = 0
                binding.layoutTariffWeek.background = ContextCompat.getDrawable(requireContext(), R.drawable.selected_tariff)
                binding.layoutTariffMonth.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
                binding.layoutTariff3Month.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
                binding.layoutTariff6Month.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
                binding.layoutTariffYear.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
            }
            1 ->{
                tariffID = 1
                binding.layoutTariffWeek.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
                binding.layoutTariffMonth.background = ContextCompat.getDrawable(requireContext(), R.drawable.selected_tariff)
                binding.layoutTariff3Month.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
                binding.layoutTariff6Month.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
                binding.layoutTariffYear.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
            }
            2 ->{
                tariffID = 2
                binding.layoutTariffWeek.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
                binding.layoutTariffMonth.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
                binding.layoutTariff3Month.background = ContextCompat.getDrawable(requireContext(), R.drawable.selected_tariff)
                binding.layoutTariff6Month.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
                binding.layoutTariffYear.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
            }
            3 ->{
                tariffID = 3
                binding.layoutTariffWeek.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
                binding.layoutTariffMonth.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
                binding.layoutTariff3Month.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
                binding.layoutTariff6Month.background = ContextCompat.getDrawable(requireContext(), R.drawable.selected_tariff)
                binding.layoutTariffYear.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
            }
            4 ->{
                tariffID = 4
                binding.layoutTariffWeek.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
                binding.layoutTariffMonth.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
                binding.layoutTariff3Month.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
                binding.layoutTariff6Month.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
                binding.layoutTariffYear.background = ContextCompat.getDrawable(requireContext(), R.drawable.selected_tariff)
            }
        }
    }

    private fun addTextsToJsonObject(tariff: Int, email: String, name: String) : JSONObject {
        val jsonObject = JSONObject()

        val tariffName = listOf("week", "month", "3_months", "6_months", "year")
        val tariffAmount = listOf("29.00", "99.00", "249.00", "429.00", "799.00")

        jsonObject.put("tariff",tariffName[tariff])
        jsonObject.put("amount",tariffAmount[tariff])
        jsonObject.put("email",email)
        if (name.isNotEmpty()){
            jsonObject.put("name",name)
        }
        return jsonObject
    }

    private fun sendToServer(jsonObject: JSONObject, serverUrl: String) {

        val mediaType = "application/json".toMediaTypeOrNull()
        val requestBody = jsonObject.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(serverUrl)
            .post(requestBody)
            .build()

        // Usage in Coroutine Scope
        lifecycleScope.launch {
            try {
                val response = uploadFile(request)

                response.onSuccess {
                    val sharedPreferences = requireContext().getSharedPreferences("remove_tariff", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putInt("tariff_ID", tariffID)
                    editor.putBoolean("tariff_status", false)
                    editor.apply()
                }.onFailure {
                    Toast.makeText(requireContext(), R.string.error_response_message, Toast.LENGTH_SHORT).show()
                    println("Upload failed: ${it.message}")
                }

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun uploadFile(request: Request): Result<String> {
        return withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    Result.success("success")
                } else {
                    Result.failure(IOException("Error: ${response.message}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }




}
