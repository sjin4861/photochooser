package com.example.photochooser.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.photochooser.R

class GalleryActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_IMAGE_URI = "selected_image_uri"

        fun newIntent(context: Context): Intent {
            return Intent(context, GalleryActivity::class.java)
        }
    }

    private val imagePickerActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val selectedImageUri: Uri? = result.data?.data
            if (selectedImageUri != null) {
                val dataIntent = Intent().apply {
                    data = selectedImageUri
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    putExtra(EXTRA_IMAGE_URI, selectedImageUri.toString())
                }
                setResult(Activity.RESULT_OK, dataIntent)
                finish()
            } else {
                Toast.makeText(this, "이미지를 선택하는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        } else {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        openGalleryForImage()
    }

    private fun openGalleryForImage() {
        Toast.makeText(this, "갤러리로 이동합니다!!", Toast.LENGTH_SHORT).show()
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
        }
        imagePickerActivityResultLauncher.launch(intent)
    }
}