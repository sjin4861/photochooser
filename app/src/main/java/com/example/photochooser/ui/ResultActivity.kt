package com.example.photochooser.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.photochooser.R
import android.net.Uri
import android.widget.ImageView

class ResultActivity : AppCompatActivity() {
    private lateinit var resultImageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val resultTextView = findViewById<TextView>(R.id.resultTextView)
        val backButton = findViewById<Button>(R.id.backButton)

        var result = intent.getStringExtra("RESULT_TEXT")
        if (result != null && result.contains("첫 번째")) {
            // intent에서 받은 image1을  resultimageview에 보여줌
            resultImageView = findViewById<ImageView>(R.id.resultImageView)
            // uri string을 받아서 resultimageview에 보여줌
            val uri = Uri.parse(intent.getStringExtra("image1"))
            resultImageView.setImageURI(uri)
        }
        else{
            // intent에서 받은 image2을  resultimageview에 보여줌
            resultImageView = findViewById<ImageView>(R.id.resultImageView)
            // uri string을 받아서 resultimageview에 보여줌
            val uri = Uri.parse(intent.getStringExtra("image2"))
            resultImageView.setImageURI(uri)
        }

        resultTextView.text = result

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()  // 현재 액티비티를 종료
        }
    }
}
