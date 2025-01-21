package com.example.setwallpaper

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
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.setwallpaper.databinding.FragmentAddDetailBinding
import java.io.InputStream

class AddThemeFragment : Fragment(R.layout.fragment_add_detail) {

    private lateinit var binding: FragmentAddDetailBinding
    private var clickedImageView: ImageView? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddDetailBinding.bind(view)

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
                        binding.contactNumber.visibility = View.VISIBLE
                        binding.contactNumber.hint = "+123456789"
                        binding.contactNumber.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_CLASS_PHONE
                        binding.contactNumber.text.clear()
                        Toast.makeText(requireContext(), "Selected: $selectedItem", Toast.LENGTH_SHORT).show()
                    }
                    2->{
                        binding.contactNumber.visibility = View.VISIBLE
                        binding.contactNumber.hint = "+987654321"
                        binding.contactNumber.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_CLASS_PHONE
                        binding.contactNumber.text.clear()
                        Toast.makeText(requireContext(), "Selected: $selectedItem", Toast.LENGTH_SHORT).show()
                    }
                    3->{
                        binding.contactNumber.visibility = View.VISIBLE
                        binding.contactNumber.hint = "example@ex.com"
                        binding.contactNumber.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        binding.contactNumber.text.clear()
                        Toast.makeText(requireContext(), "Selected: $selectedItem", Toast.LENGTH_SHORT).show()
                    }
                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
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
                }
                binding.lockScreen ->{
                    binding.lockScreen.setImageBitmap(bitmap)
                    binding.addLock.visibility = View.GONE
                }
                binding.notiPanel ->{
                    binding.notiPanel.setImageBitmap(bitmap)
                    binding.addNoti.visibility = View.GONE
                }
                binding.extraScreen1 ->{
                    binding.extraScreen1.setImageBitmap(bitmap)
                    binding.addExtra.visibility = View.GONE
                }
                binding.extraScreen2 ->{
                    binding.extraScreen2.setImageBitmap(bitmap)
                    binding.addExtra2.visibility = View.GONE
                }
            }
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

}