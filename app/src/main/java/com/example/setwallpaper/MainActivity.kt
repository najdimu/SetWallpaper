package com.example.setwallpaper

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.setwallpaper.databinding.ActivityMainBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var firstImage: Bitmap? = null
    private var secondImage: Bitmap? = null
    private var thirdImage: Bitmap? = null

    private var checkClick = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtnAddThemeFg.setOnClickListener {
            finish()
        }

        binding.btnAddImage.setOnClickListener {
            if(checkClick < 3){
                pickImageFromGallery()
            }
            if (checkClick == 2){
                binding.btnAddImage.visibility = View.GONE
            }

        }

        binding.btnSent.setOnClickListener {

            val description = binding.reportDescription.text.toString()
            val userName = binding.userName.text.toString()
            val userEmail = binding.userEmail.text.toString()

            if (description.isEmpty() || userName.isEmpty() || userEmail.isEmpty()
                    || checkClick == 0 ) {
                    Toast.makeText(this,R.string.please_fill, Toast.LENGTH_SHORT).show()
                }
                else {
                    uploadTextsAndImages(description, userName, userEmail, listOf(firstImage, secondImage, thirdImage))
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
            if (result.resultCode == RESULT_OK) {
                val imageUri: Uri? = result.data?.data
                imageUri?.let { uri ->
                    pictureToImageView(uri)
                }
            }
        }

        private fun pictureToImageView(uri: Uri){
            try {
                val inputStream: InputStream? = contentResolver.openInputStream(uri)
                val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
                checkClick += 1
                when (checkClick) {
                    1 ->{
                        binding.firstImage.load(bitmap){transformations(RoundedCornersTransformation(16f))}
                        binding.firstImage.visibility = View.VISIBLE
                        firstImage = bitmap
                    }
                    2 ->{
                        binding.secondImage.load(bitmap){transformations(RoundedCornersTransformation(16f))}
                        binding.secondImage.visibility = View.VISIBLE
                        secondImage = bitmap
                    }
                    3 ->{
                        binding.thirdImage.load(bitmap){transformations(RoundedCornersTransformation(16f))}
                        binding.thirdImage.visibility = View.VISIBLE
                        thirdImage = bitmap
                    }
                }
                // Notify user (optional)
                Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show()
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
                        println("Response Yalnyslyk: ${response.body?.string()}")
                    } else {
                        println("Error Yalnyslyk: ${response.message}")
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
        private fun uploadTextsAndImages(description: String, name: String, email: String,
                                         bitmaps: List<Bitmap?>) {
            val imageArray = JSONArray()
            when (checkClick) {
                1 ->{
                    val base64Image = bitmaps[0]?.let { convertBitmapToBase64(it) }
                    imageArray.put(base64Image)
                }
                2 -> {
                    for (i in 0..1) {
                        val base64Image = bitmaps[i]?.let { convertBitmapToBase64(it) }
                        imageArray.put(base64Image)
                    }
                }
                3 ->{
                    for (bitmap in bitmaps){
                        val base64Image = bitmap?.let { convertBitmapToBase64(it) }
                        imageArray.put(base64Image)
                    }
                }
            }
            val jsonObject = JSONObject()
            jsonObject.put("description",description)
            jsonObject.put("name",name)
            jsonObject.put("email",email)
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