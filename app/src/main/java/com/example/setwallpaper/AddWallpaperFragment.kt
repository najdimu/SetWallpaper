package com.example.setwallpaper

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.setwallpaper.databinding.FragmentAddWallpaperBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class AddWallpaperFragment : Fragment(R.layout.fragment_add_wallpaper) {

    private lateinit var binding: FragmentAddWallpaperBinding

    private var tariffID = -1
    private var bitmapWallpaper: Bitmap? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddWallpaperBinding.bind(view)

        binding.backBtnAddWallFg.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.containerFg, MainFragment())
                .commit()
        }

        binding.layoutTariffWeek.setOnClickListener { checkTariff(1) }
        binding.layoutTariffMonth.setOnClickListener { checkTariff(2) }
        binding.layoutTariff3Month.setOnClickListener { checkTariff(3) }
        binding.layoutTariff6Month.setOnClickListener { checkTariff(4) }
        binding.layoutTariffYear.setOnClickListener { checkTariff(5) }

        TODO("tariflaring subtext gorip chikmali" +
                "check box true bolmasa error text chikmali" +
                "activity ishlamali tarif saylamak, cheking, btn buy" +
                "birinji page ishlamali")





//        binding.btnSendWallpaper.setOnClickListener {
//            val wallpaperName = binding.contentName.text.toString()
//            val designerName = binding.designerName.text.toString()
//            val wallpaperSize = binding.contentSize.text.toString()
//            val userContact = binding.howContactEditText.text.toString()
//
//            if (wallpaperName.isEmpty() || wallpaperSize.isEmpty() || userContact.isEmpty()
//                || colorId == -1 || bitmapWallpaper == null) {
//                Toast.makeText(requireContext(),R.string.please_fill, Toast.LENGTH_SHORT).show()
//            }
//            else {
//                try{
//                    val bitmapByteArray = bitmapsToListByteArray(bitmapWallpaper)
//                    val jsonObject = addTextsToJsonObject(wallpaperName, designerName, wallpaperSize, userContact, colorsName[colorId])
//                    val jsonByteArray = jsonToByteArray(jsonObject)
//                    val zipFile = createZipInMemory(jsonByteArray, bitmapByteArray)
//                    sendToServerZip(zipFile, "https://yourserver.com/api/upload/theme")
//                    binding.progressBar.visibility = View.VISIBLE
//                    binding.btnSendWallpaper.text = ""
//                    binding.btnSendWallpaper.isEnabled = false
//
//                }catch  (e: Exception) {
//                    buttonClickTrue()
//                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
//                    e.printStackTrace()
//                }
//            }
//        }


    }


    private fun checkTariff(layout: Int){
        when (layout){
            1 ->{
                tariffID = 1
                binding.layoutTariffWeek.background = ContextCompat.getDrawable(requireContext(), R.drawable.selected_tariff)
                binding.layoutTariffMonth.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
                binding.layoutTariff3Month.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
                binding.layoutTariff6Month.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
                binding.layoutTariffYear.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
            }
            2 ->{
                tariffID = 2
                binding.layoutTariffWeek.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
                binding.layoutTariffMonth.background = ContextCompat.getDrawable(requireContext(), R.drawable.selected_tariff)
                binding.layoutTariff3Month.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
                binding.layoutTariff6Month.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
                binding.layoutTariffYear.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
            }
            3 ->{
                tariffID = 3
                binding.layoutTariffWeek.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
                binding.layoutTariffMonth.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
                binding.layoutTariff3Month.background = ContextCompat.getDrawable(requireContext(), R.drawable.selected_tariff)
                binding.layoutTariff6Month.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
                binding.layoutTariffYear.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
            }
            4 ->{
                tariffID = 4
                binding.layoutTariffWeek.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
                binding.layoutTariffMonth.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
                binding.layoutTariff3Month.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
                binding.layoutTariff6Month.background = ContextCompat.getDrawable(requireContext(), R.drawable.selected_tariff)
                binding.layoutTariffYear.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
            }
            5 ->{
                tariffID = 5
                binding.layoutTariffWeek.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
                binding.layoutTariffMonth.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
                binding.layoutTariff3Month.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
                binding.layoutTariff6Month.background = ContextCompat.getDrawable(requireContext(), R.drawable.unselected_tariff)
                binding.layoutTariffYear.background = ContextCompat.getDrawable(requireContext(), R.drawable.selected_tariff)
            }
        }
    }





    private fun bitmapsToListByteArray(bitmap: Bitmap?, format: Bitmap.CompressFormat =
        Bitmap.CompressFormat.JPEG, quality: Int = 75): ByteArray {
        return bitmap?.let {
            val stream = ByteArrayOutputStream()
            it.compress(format, quality, stream)
            stream.toByteArray()
        } ?: ByteArray(0) // Return an empty ByteArray for null Bitmaps

    }

    private fun addTextsToJsonObject(name: String, designer: String, size: String,
                                     contact: String, color: String) : JSONObject {
        val jsonObject = JSONObject()

        jsonObject.put("name",name)
        if (designer.isNotEmpty()){
            jsonObject.put("designer",designer)
        }
        jsonObject.put("size",size)
        jsonObject.put("contact",contact)
        jsonObject.put("color",color)
        return jsonObject
    }

    private fun jsonToByteArray(jsonObject: JSONObject): ByteArray {
        return jsonObject.toString().toByteArray(Charsets.UTF_8)
    }

    private fun createZipInMemory(jsonBytes: ByteArray, imageBytes: ByteArray): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val zipOutputStream = ZipOutputStream(byteArrayOutputStream)

        // Add JSON to ZIP
        zipOutputStream.putNextEntry(ZipEntry("data.json"))
        zipOutputStream.write(jsonBytes)
        zipOutputStream.closeEntry()

        // Add images to ZIP
        zipOutputStream.putNextEntry(ZipEntry("image_.jpg"))
        zipOutputStream.write(imageBytes)
        zipOutputStream.closeEntry()


        zipOutputStream.close() // Finish ZIP
        return byteArrayOutputStream.toByteArray()
    }

    private fun sendToServerZip(zipBytes: ByteArray, serverUrl: String) {

        val requestBody = zipBytes.toRequestBody("application/zip".toMediaType())

        val request = Request.Builder()
            .url(serverUrl)
            .post(requestBody)
            .build()

        // Usage in Coroutine Scope
        lifecycleScope.launch {
            try {
                val response = uploadFile(request)

                response.onSuccess {
                    val targetFragment = ResponseFragment()
                    targetFragment.arguments = Bundle().apply { putBoolean("message_result", false) }
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.containerFg, targetFragment)
                        .commit()

                }.onFailure {
                  //  buttonClickTrue()
                    Toast.makeText(requireContext(), R.string.error_response_message, Toast.LENGTH_SHORT).show()
                    println("Upload failed: ${it.message}")
                }

            } catch (e: Exception) {
               // buttonClickTrue()
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
