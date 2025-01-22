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
import com.example.setwallpaper.databinding.FragmentAddWallpaperBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okio.IOException
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream

class AddWallpaperFragment : Fragment(R.layout.fragment_add_wallpaper) {

    private lateinit var binding: FragmentAddWallpaperBinding

    private var colorId = -1
    private var addWallCheck = true

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


        val items = listOf("Choose an option", "Telegram", "WhatsApp", "Email")
        val adapter = object: ArrayAdapter<String>(requireContext(), R.layout.layout, items){
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view as TextView

                // Set the color of the disabled item
                if (position == 0) {
                    textView.setTextColor(resources.getColor(R.color.rang4, null))
                } else {
                    textView.setTextColor(resources.getColor(R.color.white1, null))
                }
                return view
            }
        }

        binding.spinner.adapter = adapter

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val selectedItem = p0?.getItemAtPosition(p2).toString()
                when (p2){
                    1->{
                        binding.howContactEditText.visibility = View.VISIBLE
                        binding.howContactEditText.hint = "username"
                        binding.howContactEditText.inputType = android.text.InputType.TYPE_CLASS_TEXT
                        binding.howContactEditText.text.clear()
                        Toast.makeText(requireContext(), "Selected: $selectedItem", Toast.LENGTH_SHORT).show()
                    }
                    2->{
                        binding.howContactEditText.visibility = View.VISIBLE
                        binding.howContactEditText.hint = "+888 00 000 000 0"
                        binding.howContactEditText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_CLASS_PHONE
                        binding.howContactEditText.text.clear()
                        Toast.makeText(requireContext(), "Selected: $selectedItem", Toast.LENGTH_SHORT).show()
                    }
                    3->{
                        binding.howContactEditText.visibility = View.VISIBLE
                        binding.howContactEditText.hint = "example@ex.com"
                        binding.howContactEditText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        binding.howContactEditText.text.clear()
                        Toast.makeText(requireContext(), "Selected: $selectedItem", Toast.LENGTH_SHORT).show()
                    }
                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }


        binding.btnSubmit.setOnClickListener {
            val wallpaperName = binding.contentName.text.toString()
            val designerName = binding.designerName.text.toString()
            val wallpaperSize = binding.contentSize.text.toString()
            val userContact = binding.howContactEditText.text.toString()
            val wallpaper = binding.wallpaperImageView.drawable

            if (wallpaperName.isEmpty() || wallpaperSize.isEmpty() || userContact.isEmpty()
                || colorId == -1 || addWallCheck) {
                Toast.makeText(requireContext(),"Please fill all information", Toast.LENGTH_SHORT).show()
            }
            else {
                uploadTextsAndImages(wallpaperName, designerName,
                    wallpaperSize, userContact, colorsName[colorId], wallpaper)
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
            binding.wallpaperImageView.setImageBitmap(bitmap)
            binding.wallpaperTextView2.visibility = View.GONE
            addWallCheck = false
            // Notify user (optional)
            Toast.makeText(requireContext(), "picture add successfully!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Failed to add picture.", Toast.LENGTH_SHORT).show()
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



    //send data to server
    private fun sendJsonToServer(jsonObject: JSONObject) {
        val client = OkHttpClient()

        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = RequestBody.create(mediaType, jsonObject.toString())

        val request = Request.Builder()
            .url("https://yourserver.com/api/upload/wallpaper") // Replace with your API endpoint
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    println("Response Yalnyslyk: ${response.body?.string()}")
                } else {
                    println("Error Yalnyslyk: ${response.message}")
                }
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {

                println("Yalnyslyk")
                e.printStackTrace()
            }
        })
    }

    // bitmap converter
    private fun convertBitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.WEBP, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun uploadTextsAndImages(name: String, designer: String, size: String,
                                     contact: String, color: String,
                                     drawable: Drawable) {
        // Convert ImageViews to Bitmaps
        val bitmap = (drawable as BitmapDrawable).bitmap
        val base64Image = convertBitmapToBase64(bitmap) // Convert image to Base64


        val jsonObject = JSONObject()
        jsonObject.put("name",name)
        jsonObject.put("size",size)
        jsonObject.put("contact",contact)
        jsonObject.put("color",color)
        jsonObject.put("images", base64Image)
        if (designer.isNotEmpty()){
            jsonObject.put("designer",designer)
        }


        // Send JSON to server
        sendJsonToServer(jsonObject)
        println("Yalnyslyk: $jsonObject")
    }


}
