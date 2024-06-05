package com.example.photochooser.ui

import android.Manifest
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.photochooser.R
import com.example.photochooser.data.api.RetrofitClient
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import java.io.ByteArrayOutputStream
import java.io.IOException
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.TextView
import android.content.Intent
import android.widget.Toast
import android.net.Uri
import android.app.Activity
import android.provider.MediaStore
// SocketTimeoutException
import java.net.SocketTimeoutException
import com.example.photochooser.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.photochooser.data.api.GptAPI

class MainActivity : AppCompatActivity() {
    companion object {
        const val SETTING_REQUEST_CODE = 1
        const val REQUEST_CODE_IMAGE1_PICK = 1002
        const val REQUEST_CODE_IMAGE2_PICK = 1003
        const val STRING_INTENT_ITEM_FROM_RECOMMEND_KEY = "selected_image_uri"
    }
    private lateinit var textView: TextView
    private lateinit var imageView1: ImageView
    private lateinit var imageView2: ImageView
    private lateinit var setting: ImageView
    private lateinit var selectedImageUri1: Uri
    private lateinit var selectedImageUri2: Uri

    private lateinit var imagePicker1: ActivityResultLauncher<Intent>
    private lateinit var imagePicker2: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView1 = findViewById(R.id.imageView1)
        imageView2 = findViewById(R.id.imageView2)
        setting = findViewById(R.id.setting)
        textView = findViewById(R.id.textView)
        val button = findViewById<Button>(R.id.button)

        imagePicker1 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                if (uri == null) {
                    println("Failed to get URI for image 1")
                    return@registerForActivityResult
                }
                selectedImageUri1 = uri
                imageView1.setImageURI(uri)
            }
        }

        imagePicker2 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                if (uri == null) {
                    println("Failed to get URI for image 2")
                    return@registerForActivityResult
                }
                selectedImageUri2 = uri
                imageView2.setImageURI(uri)
            }
        }

        imageView1.setOnClickListener {
            openGalleryForImage(REQUEST_CODE_IMAGE1_PICK)
        }

        imageView2.setOnClickListener {
            openGalleryForImage(REQUEST_CODE_IMAGE2_PICK)
        }

        setting.setOnClickListener(){
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

        button.setOnClickListener {
            if (!::selectedImageUri1.isInitialized || !::selectedImageUri2.isInitialized) {
                textView.text = "사진을 두 개 선택해주세요."
                return@setOnClickListener
            }
            CoroutineScope(Dispatchers.Main).launch {

                val image1 = withContext(Dispatchers.IO) {
                    "data:image/jpeg;base64," + encodeImageViewToBase64(this@MainActivity, imageView1)
                }
                val image2 = withContext(Dispatchers.IO) {
                    "data:image/jpeg;base64," + encodeImageViewToBase64(this@MainActivity, imageView2)
                }

                println(image1.substring(0, 100))
                println(image2.substring(0, 100))

                getRecommendation(image1, image2)
            }
        }
    }
    private fun openGalleryForImage(requestCode: Int) {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        when (requestCode) {
            REQUEST_CODE_IMAGE1_PICK -> imagePicker1.launch(intent)
            REQUEST_CODE_IMAGE2_PICK -> imagePicker2.launch(intent)
        }
    }

    private fun getRecommendation(image1: String, image2: String) {
        val json = JsonObject().apply {
            addProperty("model", "gpt-4o")
            add("messages", JsonArray().apply {
                add(JsonObject().apply {
                    addProperty("role", "system")
                    add("content", JsonArray().apply {
                        add(JsonObject().apply {
                            addProperty("type", "text")
                            addProperty("text", Constants.INSTA_RECOMMEND)
                        })
                    })
                })
                add(JsonObject().apply {
                    addProperty("role", "user")
                    add("content", JsonArray().apply {
                        add(JsonObject().apply {
                            addProperty("type", "image_url")
                            add("image_url", JsonObject().apply {
                                addProperty("url", image1)
                            })
                        })
                        add(JsonObject().apply {
                            addProperty("type", "image_url")
                            add("image_url", JsonObject().apply {
                                addProperty("url", image2)
                            })
                        })
                    })
                })
            })
            addProperty("temperature", Constants.TEMPERATURE)
            addProperty("max_tokens", Constants.MAX_TOKENS)
            addProperty("top_p", 1)
            addProperty("frequency_penalty", 0)
            addProperty("presence_penalty", 0)
        }
        println(Constants.TEMPERATURE)
        println(Constants.MAX_TOKENS)

        val apiService = RetrofitClient.instance
        val call = apiService.getRecommendation(json)

        call.enqueue(object : Callback<JsonObject> {
            val textView = findViewById<TextView>(R.id.textView)

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                if (t is SocketTimeoutException) {
                    runOnUiThread {
                        textView.text = "네트워크 요청 시간이 초과되었습니다. 다시 시도해 주세요."
                    }
                } else {
                    t.printStackTrace()
                }
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val responseData = response.body()
                    // responsedata의 text부분만 출력
                    println(responseData)
                    val assistantObject = responseData?.getAsJsonArray("choices")?.get(0)?.asJsonObject?.getAsJsonObject("message")
                    val text = assistantObject?.getAsJsonPrimitive("content")?.asString
                    textView.text = "응답 수신 완료, 곧 결과 창이 뜹니다."
                    println(text)
                    // Start ResultActivity with the response text
                    val intent = Intent(this@MainActivity, ResultActivity::class.java)
                    intent.putExtra("RESULT_TEXT", text)
                    startActivity(intent)
                } else {
                    println("Request failed: ${response.code()}")
                }
            }
        })
    }
    private fun encodeImageViewToBase64(context: Context, imageView: ImageView): String? {
        // ImageView에서 drawable 가져오기
        val drawable = imageView.drawable ?: return null
        if (drawable !is BitmapDrawable) {
            return null
        }
        val bitmap = drawable.bitmap

        // Bitmap을 리사이즈하여 성능 개선
        val resizedBitmap = resizeBitmap(bitmap, 600, 600) // 원하는 크기로 조정

        // Bitmap을 ByteArray로 변환
        val byteArrayOutputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream) // 압축률 80%로 조정
        val byteArray = byteArrayOutputStream.toByteArray()

        // ByteArray를 Base64로 인코딩
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    // Bitmap 크기를 조정하는 함수
    private fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        var width = bitmap.width
        var height = bitmap.height

        if (width > maxWidth || height > maxHeight) {
            val ratioBitmap = width.toFloat() / height.toFloat()
            if (ratioBitmap > 1) {
                width = maxWidth
                height = (width / ratioBitmap).toInt()
            } else {
                height = maxHeight
                width = (height * ratioBitmap).toInt()
            }
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }
}