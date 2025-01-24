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
import coil.Coil
import coil.load
import com.example.setwallpaper.databinding.FragmentAddThemeBinding
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

class AddThemeFragment : Fragment(R.layout.fragment_add_theme) {

    private lateinit var binding: FragmentAddThemeBinding
    private var clickedImageView: ImageView? = null
    private var addHomeScr = true
    private var addLockScr = true
    private var addNotiPanel = true
    private var addExtraScr1 = false
    private var addExtraScr2 = false
    private var colorId = -1


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddThemeBinding.bind(view)

        binding.backBtnAddThemeFg.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.containerFg, WhichDeviceFragment())
                .commit()
        }

        val deviceName = arguments?.getString("device")
        if (!deviceName.isNullOrEmpty()){
            binding.toolbarTitleAddTheme.text = deviceName
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
            binding.chooseColorTitle.setTextColor(Color.parseColor(selectedColor))
            updateButtonBackground(binding.btnRadioGroup, checkedId)
        }

        binding.homeScreen.setOnClickListener {
            clickedImageView = binding.homeScreen
            pickImageFromGallery()
        }
        binding.lockScreen.setOnClickListener {
            clickedImageView = binding.lockScreen
            pickImageFromGallery()
        }
        binding.notiPanel.setOnClickListener {
            clickedImageView = binding.notiPanel
            pickImageFromGallery()
        }
        binding.extraScreen1.setOnClickListener {
            clickedImageView = binding.extraScreen1
            pickImageFromGallery()
        }
        binding.extraScreen2.setOnClickListener {
            clickedImageView = binding.extraScreen2
            pickImageFromGallery()
        }

        val items = listOf(getString(R.string.choose_spinner_option), "Telegram", "WhatsApp", "Email")
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
                when (p2){
                    1->{
                        binding.howContactEditTextTheme.visibility = View.VISIBLE
                        binding.howContactEditTextTheme.hint = "username"
                        binding.howContactEditTextTheme.inputType = android.text.InputType.TYPE_CLASS_TEXT
                        binding.howContactEditTextTheme.text.clear()
                    }
                    2->{
                        binding.howContactEditTextTheme.visibility = View.VISIBLE
                        binding.howContactEditTextTheme.hint = "+888 00 000 000 0"
                        binding.howContactEditTextTheme.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_CLASS_PHONE
                        binding.howContactEditTextTheme.text.clear()
                    }
                    3->{
                        binding.howContactEditTextTheme.visibility = View.VISIBLE
                        binding.howContactEditTextTheme.hint = "example@ex.com"
                        binding.howContactEditTextTheme.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        binding.howContactEditTextTheme.text.clear()
                    }
                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }


        binding.btnSubmit.setOnClickListener {
            val themeName = binding.contentNameTheme.text.toString()
            val designerName = binding.designerNameTheme.text.toString()
            val themeSize = binding.contentSizeTheme.text.toString()
            val themeLink = binding.contentLinkTheme.text.toString()
            val userContact = binding.howContactEditTextTheme.text.toString()

            val homeScreen = binding.homeScreen.drawable
            val lockScreen = binding.lockScreen.drawable
            val notiPanel = binding.notiPanel.drawable
            val extra1 = binding.extraScreen1.drawable
            val extra2 = binding.extraScreen2.drawable

            val imagesWithExtra = listOf(homeScreen, lockScreen, notiPanel, extra1, extra2)
            val imagesNoExtra = listOf(homeScreen, lockScreen, notiPanel)

            if (themeName.isEmpty() || themeSize.isEmpty() || themeLink.isEmpty()
                || userContact.isEmpty() || colorId == -1 || addHomeScr || addLockScr || addNotiPanel ) {
                Toast.makeText(requireContext(),R.string.please_fill, Toast.LENGTH_SHORT).show()
            }
            else {
                if (addExtraScr1 && addExtraScr2){
                   uploadTextsAndImages(themeName, designerName,themeSize, themeLink, userContact, colorsName[colorId], imagesWithExtra)
                }
                else{
                    uploadTextsAndImages(themeName, designerName,themeSize, themeLink, userContact, colorsName[colorId], imagesNoExtra)
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
            imageUri?.let { uri ->
                clickedImageView?.let { pictureToImageView(it,uri) }
            }
        }
    }

    private fun pictureToImageView(view: ImageView, uri: Uri){
        try {
            val inputStream: InputStream? = requireActivity().contentResolver.openInputStream(uri)
            val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)

            when (view) {
                binding.homeScreen ->{
                    binding.homeScreen.setImageBitmap(bitmap)
                    binding.addHome.visibility = View.GONE
                    addHomeScr = false
                }
                binding.lockScreen ->{
                    binding.lockScreen.setImageBitmap(bitmap)
                    binding.addLock.visibility = View.GONE
                    addLockScr = false
                }
                binding.notiPanel ->{
                    binding.notiPanel.setImageBitmap(bitmap)
                    binding.addNoti.visibility = View.GONE
                    addNotiPanel = false
                }
                binding.extraScreen1 ->{
                    binding.extraScreen1.setImageBitmap(bitmap)
                    binding.addExtra.visibility = View.GONE
                    addExtraScr1 = true
                }
                binding.extraScreen2 ->{
                    binding.extraScreen2.setImageBitmap(bitmap)
                    binding.addExtra2.visibility = View.GONE
                    addExtraScr2 = true
                }
            }
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
            setColor(Color.parseColor(colorHex)) // Set the fill color
            setStroke(4, android.graphics.Color.BLACK) // Add a stroke
        }
    }

    private fun createUnselectedColor(colorHex: String): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.parseColor(colorHex)) // Set the fill color for unselected
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
        val requestBody = jsonObject.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://yourserver.com/api/upload/theme") // Replace with your API endpoint
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    println("Response: ${response.body?.string()}")
                } else {
                    println("Error: ${response.message}")
                }
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
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
                                     link: String, contact: String, color: String,
                                     drawables: List<Drawable>) {
        // Convert ImageViews to Bitmaps
        val bitmaps = drawables.map { drawable ->
            (drawable as BitmapDrawable).bitmap
        }
        val imageArray = JSONArray()
        for (image in bitmaps) {
            val base64Image = convertBitmapToBase64(image) // Convert image to Base64
            imageArray.put(base64Image)
        }

        val jsonObject = JSONObject()
        jsonObject.put("name",name)
        if (designer.isNotEmpty()){
            jsonObject.put("designer",designer)
        }
        jsonObject.put("size",size)
        jsonObject.put("link",link)
        jsonObject.put("contact",contact)
        jsonObject.put("color",color)
        jsonObject.put("images", imageArray)
        // Send JSON to server
        sendJsonToServer(jsonObject)
    }

        private fun checkBitmap(drawable: Drawable, imageView: ImageView) {
            // Get Bitmap from ImageView
            val bitmapFromImageView = (drawable as BitmapDrawable).bitmap

            // Convert Bitmap to Base64 String
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmapFromImageView.compress(Bitmap.CompressFormat.WEBP, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            val base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT)

            // Convert Base64 String back to Bitmap
            val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
            val bitmapFromBase64 = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

            // Set ImageView with Bitmap from Base64
            imageView.setImageBitmap(bitmapFromBase64)
        }


}