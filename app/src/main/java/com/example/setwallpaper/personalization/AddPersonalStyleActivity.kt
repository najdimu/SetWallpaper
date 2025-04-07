package com.example.setwallpaper.personalization

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.setwallpaper.R
import com.example.setwallpaper.databinding.ActivityAddPersonalStyleBinding
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

class AddPersonalStyleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPersonalStyleBinding


    private var clickedImageView: ImageView? = null
    private var bitmapSrc1: Bitmap? = null
    private var bitmapScr2: Bitmap? = null
    private var bitmapScr3: Bitmap? = null
    private var bitmapAvatar: Bitmap? = null

    private var addScr1 = true
    private var addAvatar = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddPersonalStyleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.add_personal_style_activity_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = binding.toolbarAddPersonalStyle
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) ?: true
        toolbar.setNavigationOnClickListener {
            finish()
        }
        title = resources.getString(R.string.toolbar_title_personal_style)//
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        val colorFilter = PorterDuffColorFilter(
            ContextCompat.getColor(this, R.color.white1),
            PorterDuff.Mode.SRC_ATOP
        )
        toolbar.navigationIcon?.colorFilter = colorFilter

        binding.addScreenshots1.setOnClickListener {
            clickedImageView = binding.addScreenshots1
            pickImageFromGallery()
        }
        binding.addScreenshots2.setOnClickListener {
            clickedImageView = binding.addScreenshots2
            pickImageFromGallery()
        }
        binding.addScreenshots3.setOnClickListener {
            clickedImageView = binding.addScreenshots3
            pickImageFromGallery()
        }
        binding.userAvatar.setOnClickListener {
            clickedImageView = binding.userAvatar
            pickImageFromGallery()
        }

        binding.btnSendContent.setOnClickListener {
            val title = binding.editTextTitle.text.toString()
            val description = binding.editTextDescription.text.toString()
            val userName = binding.editTextUsername.text.toString()

            val bitmapList = listOf(bitmapSrc1, bitmapScr2, bitmapScr3, bitmapAvatar)

            if (title.isEmpty() || description.isEmpty() || userName.isEmpty()
                || addScr1 || addAvatar ) {
                Toast.makeText(this, "R.string.please_fill", Toast.LENGTH_SHORT).show()
            }
            else {
                try{
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSendContent.text = ""
                    binding.btnSendContent.isEnabled = false
                    val bitmapByteArrayList = bitmapsToListByteArray(bitmapList)
                    val jsonObject = addTextsToJsonObject(title, description, userName)
                    val jsonByteArray = jsonToByteArray(jsonObject)
                    val zipFile = createZipInMemory(jsonByteArray, bitmapByteArrayList)
                    sendToServerZip(zipFile, "https://yourserver.com/api/upload/theme")

                }catch  (e: Exception) {
                    buttonClickTrue()
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        }  // good
    }

    private fun pickImageFromGallery() {
        val pickImageIntent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        galleryLauncher.launch(pickImageIntent)
    } // good

    // Handle the result from the gallery
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            imageUri?.let { uri ->
                clickedImageView?.let { pictureToImageView(it,uri) }
            }
        }
    } // good

    private fun pictureToImageView(view: ImageView, uri: Uri){
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)

            when (view) {
                binding.addScreenshots1 ->{
                    binding.addScreenshots1.setImageBitmap(bitmap)
                    binding.icAddScreenshots1.visibility = View.GONE
                    binding.textAddScreenshots1.visibility = View.GONE
                    bitmapSrc1 = bitmap
                    addScr1 = false
                }
                binding.addScreenshots2 ->{
                    binding.addScreenshots2.setImageBitmap(bitmap)
                    binding.icAddScreenshots2.visibility = View.GONE
                    binding.textAddScreenshots2.visibility = View.GONE
                    bitmapScr2 = bitmap
                }
                binding.addScreenshots3 ->{
                    binding.addScreenshots3.setImageBitmap(bitmap)
                    binding.icAddScreenshots3.visibility = View.GONE
                    binding.textAddScreenshots3.visibility = View.GONE
                    bitmapScr3 = bitmap
                }
                binding.userAvatar ->{
                    binding.userAvatar.setImageBitmap(bitmap)
                    bitmapAvatar = bitmap
                    addAvatar = false
                }

            }
            // Notify user (optional)
            Toast.makeText(this, "R.string.success", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "R.string.error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resizeBitmapWidth(bitmap: Bitmap, newWidth: Int): Bitmap {
        // Calculate the new height to maintain the aspect ratio
        val aspectRatio = bitmap.height.toFloat() / bitmap.width
        val newHeight = (newWidth * aspectRatio).toInt()

        // Scale the bitmap
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    private fun bitmapsToListByteArray(bitmaps: List<Bitmap?>, format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
                                       quality: Int = 75): List<ByteArray> {
        return bitmaps.map { bitmap ->
            bitmap?.let {
                val stream = ByteArrayOutputStream()
                it.compress(format, quality, stream)
                stream.toByteArray()
            } ?: ByteArray(0) // Return an empty ByteArray for null Bitmaps
        }
    }

    private fun addTextsToJsonObject(title: String, description: String, userName: String) : JSONObject {
        val jsonObject = JSONObject()

        jsonObject.put("title",title)
        jsonObject.put("description",description)
        jsonObject.put("userName",userName)
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
                    startActivity(Intent(this@AddPersonalStyleActivity, ResponseSendStyleActivity::class.java))
                    finish()
                }.onFailure {
                    buttonClickTrue()
                    Toast.makeText(this@AddPersonalStyleActivity, "R.string.error_response_message", Toast.LENGTH_SHORT).show()
                    println("Upload failed: ${it.message}")
                }

            } catch (e: Exception) {
                buttonClickTrue()
                Toast.makeText(this@AddPersonalStyleActivity, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
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
        binding.btnSendContent.text = "R.string.send_content_"
        binding.btnSendContent.isEnabled = true
    }

//    private fun sendJsonToServer(jsonObject: JSONObject) {
//
//        val firebaseRemoteConfig2 = FirebaseRemoteConfig.getInstance()
//        val serverUrlReport = firebaseRemoteConfig2.getString("server_url")
//
//        val client = OkHttpClient()


//        // Создаем тело запроса multipart/form-data
//        val requestBody = MultipartBody.Builder()
//            .setType(MultipartBody.FORM)
//            // Добавляем JSON как текстовое поле
//            .addFormDataPart(
//                "report_json",
//                jsonObject.toString()
//            )
//            // Добавляем каждый Bitmap как отдельный файл
//            .apply {
//                bitmaps.forEachIndexed { index, bitmap ->
//                    val bitmapBytes = convertBitmapToByteArray(bitmap)
//                    val fileName = "${randomNumber}_${fileName()}_$index.jpg"
//                    addFormDataPart(
//                        "images", // Ключ, который сервер ожидает для файлов
//                        fileName, // Имя файла
//                        bitmapBytes.toRequestBody("image/jpeg".toMediaType()) // MIME-тип для JPEG
//                    )
//                }
//            }
//            .build()
//
//        val request = Request.Builder()
//            .url("$serverUrlReport/themes-app-user-report") // Replace with your API endpoint
//            .post(requestBody)
//            .build()
//
//        client.newCall(request).enqueue(object : okhttp3.Callback {
//            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
//                sendingStatus = if (response.isSuccessful) {
//                    if (response.body?.string() == "ok") {
//                        runOnUiThread {
//                            binding.btnSent.text = resources.getString(R.string.tt_done)
//
//                            binding.doneErrorText.visibility = View.VISIBLE
//                            binding.doneErrorText.text = resources.getString(R.string.sent_your_report)
//
//                            val currentTime = System.currentTimeMillis()
//                            sharedPreference?.edit()?.putLong("last_rate_shown_time", currentTime)?.apply()
//
//                            binding.btnSent.visibility = View.VISIBLE
//                            binding.progressBar15.visibility = View.GONE
//                        }
//                        2
//                    } else {
//                        runOnUiThread {
//                            binding.reportForm.visibility = View.GONE
//                            binding.doneErrorText.visibility = View.VISIBLE
//                            binding.doneErrorText.text = resources.getString(R.string.unable_to_send_your_message)
//
//                            binding.btnSent.visibility = View.VISIBLE
//                            binding.progressBar15.visibility = View.GONE
//                        }
//                        1
//                    }
//                } else {
//                    runOnUiThread {
//                        binding.reportForm.visibility = View.GONE
//                        binding.doneErrorText.visibility = View.VISIBLE
//                        binding.doneErrorText.text = resources.getString(R.string.unable_to_send_your_message)
//
//                        binding.btnSent.visibility = View.VISIBLE
//                        binding.progressBar15.visibility = View.GONE
//                    }
//                    1
//                }
//            }
//
//            override fun onFailure(call: okhttp3.Call, e: IOException) {
//                runOnUiThread {
//                    binding.reportForm.visibility = View.GONE
//                    binding.doneErrorText.visibility = View.VISIBLE
//                    binding.doneErrorText.text = resources.getString(R.string.unable_to_send_your_message)
//
//                    binding.btnSent.visibility = View.VISIBLE
//                    binding.progressBar15.visibility = View.GONE
//                }
//                sendingStatus = 1
//                e.printStackTrace()
//            }
//        })
//    }



}