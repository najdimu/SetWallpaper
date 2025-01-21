package com.example.setwallpaper

import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.setwallpaper.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction().replace(R.id.containerFg, MainFragment())
            .commit()



        //getScreenSize()




    }

//    fun getScreenSize(): Pair<Int, Int> {
//        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
//        val display = windowManager.defaultDisplay
//
//        // Получаем размер экрана с использованием DisplayMetrics
//        val displayMetrics = DisplayMetrics()
//        display.getRealMetrics(displayMetrics)
//
//        // Возвращаем размер экрана (ширина и высота)
//        val pair = Pair(displayMetrics.widthPixels, displayMetrics.heightPixels)
//        Log.d("getScreenSize", pair.toString())
//        return pair
//    }

//        val btnShowDialog : Button = findViewById(R.id.set_button)
//        val view = layoutInflater.inflate(R.layout.bottom_sheet, null)
//        val btnSet = view.findViewById<CardView>(R.id.btn_set_both)
//        val btnSetHome = view.findViewById<CardView>(R.id.btn_set_home)
//        val btnSetLock = view.findViewById<CardView>(R.id.btn_set_lock)
//        // Launch gallery intent to pick an image
//        val pickImageIntent = Intent(Intent.ACTION_PICK).apply {
//            type = "image/*"
//        }
//        val dialog = BottomSheetDialog(this)
//        dialog.setContentView(view)
//
//        btnShowDialog.setOnClickListener {
//
//            dialog.show()
//
//            btnSet.setOnClickListener {
//                galleryLauncherForBoth.launch(pickImageIntent)
//                dialog.dismiss()
//            }
//            btnSetHome.setOnClickListener {
//                galleryLauncherForHome.launch(pickImageIntent)
//                dialog.dismiss()
//            }
//            btnSetLock.setOnClickListener {
//                galleryLauncherForLock.launch(pickImageIntent)
//                dialog.dismiss()
//            }
//
//            }
//
//    }
//
//    // Handle the result from the gallery
//    private val galleryLauncherForBoth =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == RESULT_OK) {
//            val imageUri: Uri? = result.data?.data
//            imageUri?.let { uri ->
//                setWallpaperFromUri(uri,1)
//            }
//        }
//    }
//
//    private val galleryLauncherForHome =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == RESULT_OK) {
//            val imageUri: Uri? = result.data?.data
//            imageUri?.let { uri ->
//                setWallpaperFromUri(uri,2)
//            }
//        }
//    }
//
//    private val galleryLauncherForLock =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == RESULT_OK) {
//            val imageUri: Uri? = result.data?.data
//            imageUri?.let { uri ->
//                setWallpaperFromUri(uri,3)
//            }
//        }
//    }
//
//    // Set the wallpaper from the selected URI
//    private fun setWallpaperFromUri(imageUri: Uri, san: Int) {
//        try {
//            val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
//            val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
//            // Set wallpaper
//            val wallpaperManager = WallpaperManager.getInstance(this)
//
//            when (san) {
//                1->{
//                    wallpaperManager.setBitmap(bitmap)
//                }
//                2->{
//                    wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM)
//                }
//                3->{
//                    wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
//                }
//            }
//
//            // Notify user (optional)
//            Toast.makeText(this, "Wallpaper set successfully!", Toast.LENGTH_SHORT).show()
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Toast.makeText(this, "Failed to set wallpaper.", Toast.LENGTH_SHORT).show()
//        }
//    }
}