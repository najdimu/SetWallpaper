package com.example.setwallpaper.publish

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.setwallpaper.R
import com.example.setwallpaper.databinding.FragmentAddWallpaperBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class AddWallpaperFragment : Fragment(R.layout.fragment_add_wallpaper) {

    private lateinit var binding: FragmentAddWallpaperBinding

    private var colorId = -1
    private var bitmapWallpaper: Bitmap? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddWallpaperBinding.bind(view)

        binding.backBtnAddWallFg.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.containerFg, MainFragment())
                .commit()
        }

        binding.wallpaperImageView.setOnClickListener {
            pickImageFromGallery()
        }

        // Dynamically create RadioButtons
        colors.forEachIndexed { index, colorHex ->
            val radioButton = RadioButton(requireContext()).apply {
                id = index // Unique ID for each button
                layoutParams = RadioGroup.LayoutParams(70, 70).apply {
                    setMargins(16, 0, 16, 0)
                }
                buttonDrawable = null // Remove default RadioButton drawable
                background = createUnselectedColor(colorHex) // Apply color selector
            }

            binding.btnRadioGroup.addView(radioButton)
        }

        // Handle selection
        binding.btnRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedColor = colors[checkedId]
                colorId = checkedId
            binding.chooseColorTitle.setTextColor(android.graphics.Color.parseColor(selectedColor))
            updateButtonBackground(binding.btnRadioGroup, checkedId)
        }

        val items = listOf(getString(R.string.choose_spinner_option), "Telegram", "WhatsApp", "Email")


        val adapter = object: ArrayAdapter<String>(requireContext(), R.layout.layout, items){
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view12 = super.getDropDownView(position, convertView, parent)
                val textView = view12 as TextView

                // Set the color of the disabled item
                if (position == 0) {
                    textView.setTextColor(resources.getColor(R.color.rang4, null))
                } else {
                    textView.setTextColor(resources.getColor(R.color.white1, null))
                }
                return view12
            }
        }

        binding.spinner.adapter = adapter

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when (p2){
                    1->{
                        binding.howContactEditText.visibility = View.VISIBLE
                        binding.howContactEditText.hint = "username"
                        binding.howContactEditText.inputType = android.text.InputType.TYPE_CLASS_TEXT
                        binding.howContactEditText.text.clear()
                    }
                    2->{
                        binding.howContactEditText.visibility = View.VISIBLE
                        binding.howContactEditText.hint = "+888 00 000 000 0"
                        binding.howContactEditText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_CLASS_PHONE
                        binding.howContactEditText.text.clear()
                    }
                    3->{
                        binding.howContactEditText.visibility = View.VISIBLE
                        binding.howContactEditText.hint = "example@ex.com"
                        binding.howContactEditText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        binding.howContactEditText.text.clear()
                    }
                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }


        binding.btnSendWallpaper.setOnClickListener {
            val wallpaperName = binding.contentName.text.toString()
            val designerName = binding.designerName.text.toString()
            val wallpaperSize = binding.contentSize.text.toString()
            val userContact = binding.howContactEditText.text.toString()

            if (wallpaperName.isEmpty() || wallpaperSize.isEmpty() || userContact.isEmpty()
                || colorId == -1 || bitmapWallpaper == null) {
                Toast.makeText(requireContext(), R.string.please_fill, Toast.LENGTH_SHORT).show()
            }
            else {
                try{
                    val bitmapByteArray = bitmapsToListByteArray(bitmapWallpaper)
                    val jsonObject = addTextsToJsonObject(wallpaperName, designerName, wallpaperSize, userContact, colorsName[colorId])
                    val jsonByteArray = jsonToByteArray(jsonObject)
                    val zipFile = createZipInMemory(jsonByteArray, bitmapByteArray)
                    sendToServerZip(zipFile, "https://yourserver.com/api/upload/theme")
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSendWallpaper.text = ""
                    binding.btnSendWallpaper.isEnabled = false

                }catch  (e: Exception) {
                    buttonClickTrue()
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        }


    }

    private fun pickImageFromGallery() {
        val pickImageIntent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        galleryLauncher.launch(pickImageIntent)
    }

    // Handle the result from the gallery
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            imageUri?.let { uri -> pictureToImageView(uri) }
            }
        }


    private fun pictureToImageView(uri: Uri){
        try {
            val inputStream: InputStream? = requireActivity().contentResolver.openInputStream(uri)
            val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
            val resizeBitmap = resizeBitmapWidth(bitmap, 720)
            binding.wallpaperImageView.setImageBitmap(resizeBitmap)
            binding.wallpaperTextView2.visibility = View.GONE
            bitmapWallpaper = bitmap
            // Notify user (optional)
            Toast.makeText(requireContext(), R.string.success, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), R.string.error, Toast.LENGTH_SHORT).show()
        }
    }

    // Define a list of colors
    private val colors = listOf(
        "#FFFFFF", // White
        "#FF0000", // Red
        "#0000FF", // Blue
        "#000000", // Black
        "#00FF00", // Green
        "#FFFF00", // Yellow
        "#FFA500", // Orange
        "#800080", // Purple
        "#00FFFF", // Cyan
        "#FB6391", // Pink
        "#A52A2A", // Brown
        "#808080"  // Gray
    )

    private val colorsName = listOf(
        "White", // White
        "Red", // Red
        "Blue", // Blue
        "Black", // Black
        "Green", // Green
        "Yellow", // Yellow
        "Orange", // Orange
        "Purple", // Purple
        "Cyan", // Cyan
        "Pink", // Pink
        "Brown", // Brown
        "Gray"  // Gray
    )

    private fun createColorSelector(colorHex: String): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(android.graphics.Color.parseColor(colorHex)) // Set the fill color
            setStroke(4, android.graphics.Color.BLACK) // Add a stroke
        }
    }

    private fun createUnselectedColor(colorHex: String): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(android.graphics.Color.parseColor(colorHex)) // Set the fill color for unselected
            setStroke(2, android.graphics.Color.WHITE) // Thin stroke for unselected state
        }
    }

    private fun updateButtonBackground(radioGroup: RadioGroup, checkedId: Int) {
        for (i in 0 until radioGroup.childCount) {
            val radioButton = radioGroup.getChildAt(i) as RadioButton
            val colorHex = colors[i]

            // Update background for each RadioButton (selected or unselected)
            if (i == checkedId) {
                radioButton.background = createColorSelector(colorHex) // Selected color
            } else {
                radioButton.background = createUnselectedColor(colorHex) // Unselected color
            }
        }
    }



    private fun resizeBitmapWidth(bitmap: Bitmap, newWidth: Int): Bitmap {
        // Calculate the new height to maintain the aspect ratio
        val aspectRatio = bitmap.height.toFloat() / bitmap.width
        val newHeight = (newWidth * aspectRatio).toInt()

        // Scale the bitmap
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
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
                    buttonClickTrue()
                    Toast.makeText(requireContext(), R.string.error_response_message, Toast.LENGTH_SHORT).show()
                    println("Upload failed: ${it.message}")
                }

            } catch (e: Exception) {
                buttonClickTrue()
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

    private fun buttonClickTrue(){
        binding.progressBar.visibility = View.GONE
        binding.btnSendWallpaper.text = getString(R.string.send_content_)
        binding.btnSendWallpaper.isEnabled = true
    }

}
