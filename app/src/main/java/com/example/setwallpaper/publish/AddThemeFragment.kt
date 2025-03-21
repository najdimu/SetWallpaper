package com.example.setwallpaper.publish

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
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
import androidx.lifecycle.lifecycleScope
import com.example.setwallpaper.R
import com.example.setwallpaper.databinding.FragmentAddThemeBinding
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

class AddThemeFragment : Fragment(R.layout.fragment_add_theme) {

    private lateinit var binding: FragmentAddThemeBinding
    private var clickedImageView: ImageView? = null
    private var bitmapHome: Bitmap? = null
    private var bitmapLock: Bitmap? = null
    private var bitmapNoti: Bitmap? = null
    private var bitmapExtra1: Bitmap? = null
    private var bitmapExtra2: Bitmap? = null
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


        binding.btnSendContent.setOnClickListener {
            val themeName = binding.contentNameTheme.text.toString()
            val designerName = binding.designerNameTheme.text.toString()
            val themeSize = binding.contentSizeTheme.text.toString()
            val themeLink = binding.contentLinkTheme.text.toString()
            val userContact = binding.howContactEditTextTheme.text.toString()

            val bitmapList = listOf(bitmapHome, bitmapLock, bitmapNoti, bitmapExtra1, bitmapExtra2)

            if (themeName.isEmpty() || themeSize.isEmpty() || themeLink.isEmpty()
                || userContact.isEmpty() || colorId == -1 || addHomeScr || addLockScr || addNotiPanel ) {
                Toast.makeText(requireContext(), R.string.please_fill, Toast.LENGTH_SHORT).show()
            }
            else {
                try{
                    val bitmapByteArrayList = bitmapsToListByteArray(bitmapList)
                    val jsonObject = addTextsToJsonObject(themeName, designerName, themeSize, themeLink, userContact, colorsName[colorId])
                    val jsonByteArray = jsonToByteArray(jsonObject)
                    val zipFile = createZipInMemory(jsonByteArray, bitmapByteArrayList)
                    sendToServerZip(zipFile, "https://yourserver.com/api/upload/theme")
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSendContent.text = ""
                    binding.btnSendContent.isEnabled = false

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
            imageUri?.let { uri ->
                clickedImageView?.let { pictureToImageView(it,uri) }
            }
        }
    }

    private fun pictureToImageView(view: ImageView, uri: Uri){
        try {
            val inputStream: InputStream? = requireActivity().contentResolver.openInputStream(uri)
            var bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)

            if (bitmap.width > 1000){
                bitmap = resizeBitmapWidth(bitmap, 1000)
            }

            when (view) {
                binding.homeScreen ->{
                    binding.homeScreen.setImageBitmap(bitmap)
                    binding.addHome.visibility = View.GONE
                    bitmapHome = bitmap
                    addHomeScr = false
                }
                binding.lockScreen ->{
                    binding.lockScreen.setImageBitmap(bitmap)
                    binding.addLock.visibility = View.GONE
                    bitmapLock = bitmap
                    addLockScr = false
                }
                binding.notiPanel ->{
                    binding.notiPanel.setImageBitmap(bitmap)
                    binding.addNoti.visibility = View.GONE
                    bitmapNoti = bitmap
                    addNotiPanel = false
                }
                binding.extraScreen1 ->{
                    binding.extraScreen1.setImageBitmap(bitmap)
                    binding.addExtra.visibility = View.GONE
                    bitmapExtra1 = bitmap
                    addExtraScr1 = true
                }
                binding.extraScreen2 ->{
                    binding.extraScreen2.setImageBitmap(bitmap)
                    binding.addExtra2.visibility = View.GONE
                    bitmapExtra2 = bitmap
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

    private fun resizeBitmapWidth(bitmap: Bitmap, newWidth: Int): Bitmap {
        // Calculate the new height to maintain the aspect ratio
        val aspectRatio = bitmap.height.toFloat() / bitmap.width
        val newHeight = (newWidth * aspectRatio).toInt()

        // Scale the bitmap
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    private fun bitmapsToListByteArray(bitmaps: List<Bitmap?>, format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG, quality: Int = 75): List<ByteArray> {
        return bitmaps.map { bitmap ->
            bitmap?.let {
                val stream = ByteArrayOutputStream()
                it.compress(format, quality, stream)
                stream.toByteArray()
            } ?: ByteArray(0) // Return an empty ByteArray for null Bitmaps
        }
    }

    private fun addTextsToJsonObject(name: String, designer: String, size: String,
                                     link: String, contact: String, color: String) : JSONObject {
        val jsonObject = JSONObject()

        jsonObject.put("name",name)
        if (designer.isNotEmpty()){
            jsonObject.put("designer",designer)
        }
        jsonObject.put("size",size)
        jsonObject.put("link",link)
        jsonObject.put("contact",contact)
        jsonObject.put("color",color)
        return jsonObject
    }

    private fun jsonToByteArray(jsonObject: JSONObject): ByteArray {
        return jsonObject.toString().toByteArray(Charsets.UTF_8)
    }

    private fun createZipInMemory(jsonBytes: ByteArray, imageBytes: List<ByteArray>): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val zipOutputStream = ZipOutputStream(byteArrayOutputStream)

        // Add JSON to ZIP
        zipOutputStream.putNextEntry(ZipEntry("data.json"))
        zipOutputStream.write(jsonBytes)
        zipOutputStream.closeEntry()

        // Add images to ZIP
        imageBytes.forEachIndexed { index, image ->
            zipOutputStream.putNextEntry(ZipEntry("image_$index.jpg"))
            zipOutputStream.write(image)
            zipOutputStream.closeEntry()
        }

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
                    targetFragment.arguments = Bundle().apply { putBoolean("message_result", true) }
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
        binding.btnSendContent.text = getString(R.string.send_content_)
        binding.btnSendContent.isEnabled = true
    }

}