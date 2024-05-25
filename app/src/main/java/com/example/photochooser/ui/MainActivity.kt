package com.example.photochooser.ui

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

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imageView1 = findViewById<ImageView>(R.id.imageView1)
        val imageView2 = findViewById<ImageView>(R.id.imageView2)
        val button = findViewById<Button>(R.id.button)

        button.setOnClickListener {
            val imageResId1 = R.drawable.cafe1
            val imageResId2 = R.drawable.cafe2

            val image1 = "data:image/jpeg;base64," + encodeImageToBase64(this, imageResId1)
            val image2 = "data:image/jpeg;base64," + encodeImageToBase64(this, imageResId2)

            // image을 1~100번째 글자까지만 출력
            println(image1.substring(0, 100))
            println(image2.substring(0, 100))

            // Call the function to get recommendation from GPT-4 API
            getRecommendation(image1, image2)
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
                add(JsonObject().apply {
                    addProperty("role", "assistant")
                    add("content", JsonArray().apply {
                        add(JsonObject().apply {
                            addProperty("type", "text")
                            addProperty("text", "I recommend posting the first image on Instagram. Here are three reasons why:\n\n1. **Variety and Visual Interest**: The first image features an array of items (sliced bread, various spreads, and drinks), which adds visual interest and variety to the photo.\n2. **Balanced Composition**: The composition of the first image is well-balanced, with different colors and elements neatly arranged, making it aesthetically pleasing.\n3. **Food Presentation**: The presentation of different spreads and bread suggests a cozy and inviting breakfast or brunch scenario, likely appealing to many people.\n\nSuggested Instagram caption: \n\"Starting the day with a delicious breakfast spread 😍🥐☕️ #BreakfastGoals #FoodieLife #MorningDelight\"\n\n")
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
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                t.printStackTrace()
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val responseData = response.body()
                    // Handle the response from the GPT-4 API
                    println(responseData)
                } else {
                    println("Request failed: ${response.code()}")
                }
            }
        })
    }

    private fun encodeImageToBase64(context: Context, resId: Int): String {
        val bitmap = BitmapFactory.decodeResource(context.resources, resId)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}
