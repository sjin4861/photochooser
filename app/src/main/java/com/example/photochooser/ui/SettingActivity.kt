package com.example.photochooser.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.photochooser.R
import com.example.photochooser.utils.Constants

class SettingActivity : AppCompatActivity() {

    private lateinit var etApiKey: EditText
    private lateinit var etTemperature: EditText
    private lateinit var etMaxTokens: EditText
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        etApiKey = findViewById(R.id.etApiKey)
        etTemperature = findViewById(R.id.etTemperature)
        etMaxTokens = findViewById(R.id.etMaxTokens)
        btnSave = findViewById(R.id.btnSave)

        // 초기 값 설정
        etApiKey.setText(Constants.API_KEY)
        etTemperature.setText(Constants.TEMPERATURE.toString())
        etMaxTokens.setText(Constants.MAX_TOKENS.toString())

        btnSave.setOnClickListener {
            // EditText에서 값을 가져와 Constants에 저장
            Constants.API_KEY = etApiKey.text.toString()
            Constants.TEMPERATURE = etTemperature.text.toString().toFloatOrNull() ?: 1.0f
            Constants.MAX_TOKENS = etMaxTokens.text.toString().toIntOrNull() ?: 2000

            // 설정 값을 저장하고 종료
            finish()
        }
    }
}