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
import com.example.photochooser.data.RetrofitClient
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

class MainActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_CODE_IMAGE1_PICK = 1002
        const val REQUEST_CODE_IMAGE2_PICK = 1003
        const val STRING_INTENT_ITEM_FROM_RECOMMEND_KEY = "selected_image_uri"
    }
    private lateinit var textView: TextView
    private lateinit var imageView1: ImageView
    private lateinit var imageView2: ImageView
    private lateinit var selectedImageUri1: Uri
    private lateinit var selectedImageUri2: Uri

    private lateinit var imagePicker1: ActivityResultLauncher<Intent>
    private lateinit var imagePicker2: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView1 = findViewById(R.id.imageView1)
        imageView2 = findViewById(R.id.imageView2)
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

        button.setOnClickListener {
            if (!::selectedImageUri1.isInitialized || !::selectedImageUri2.isInitialized) {
                textView.text = "사진을 두 개 선택해주세요."
                return@setOnClickListener
            }

            println(selectedImageUri1)
            println(selectedImageUri2)

            val image1 = "data:image/jpeg;base64," + encodeImageViewToBase64(this, imageView1)
            val image2 = "data:image/jpeg;base64," + encodeImageViewToBase64(this, imageView2)

            println(image1.substring(0, 100))
            println(image2.substring(0, 100))

            getRecommendation(image1, image2)
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
                            addProperty("text", "넌 내가 사진을 2개 입력하면 둘 중 어떤 것을 인스타그램에 올릴 지 추천해주는 비서야. 너의 추천 사진과 그 이유를 간단히 3개 정도 알려줘. 그리고 간단하게 인스타그램에 쓸 멘트도 추천해줘.")
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
            addProperty("temperature", 1)
            addProperty("max_tokens", 3000)
            addProperty("top_p", 1)
            addProperty("frequency_penalty", 0)
            addProperty("presence_penalty", 0)
        }

        val apiService = RetrofitClient.instance
        val call = apiService.getRecommendation(json)

        call.enqueue(object : Callback<JsonObject> {
            val textView = findViewById<TextView>(R.id.textView)

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                t.printStackTrace()
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

        // drawable이 BitmapDrawable인지 확인
        if (drawable !is BitmapDrawable) {
            return null
        }

        // BitmapDrawable을 Bitmap으로 변환
        val bitmap = drawable.bitmap

        // Bitmap을 ByteArray로 변환
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        // ByteArray를 Base64로 인코딩
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}