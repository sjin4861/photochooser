package com.example.photochooser.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.photochooser.R
import com.example.photochooser.utils.Constants
import com.google.android.material.slider.Slider

class SettingActivity : AppCompatActivity() {

    private lateinit var etApiKey: EditText
    private lateinit var sliderTemperature: Slider
    private lateinit var sliderMaxTokens: Slider
    private lateinit var tvTemperatureValue: TextView
    private lateinit var tvMaxTokensValue: TextView
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        etApiKey = findViewById(R.id.etApiKey)
        sliderTemperature = findViewById(R.id.sliderTemperature)
        sliderMaxTokens = findViewById(R.id.sliderMaxTokens)
        tvTemperatureValue = findViewById(R.id.tvTemperatureValue)
        tvMaxTokensValue = findViewById(R.id.tvMaxTokensValue)
        btnSave = findViewById(R.id.btnSave)

        // 초기 값 설정
        etApiKey.setText(Constants.API_KEY)
        sliderTemperature.value = Constants.TEMPERATURE.toFloat()
        sliderMaxTokens.value = Constants.MAX_TOKENS.toFloat()
        // Set initial values
        tvTemperatureValue.text = sliderTemperature.value.toString()
        tvMaxTokensValue.text = sliderMaxTokens.value.toInt().toString()

        // Add a listener to the temperature slider
        sliderTemperature.addOnChangeListener { slider, value, fromUser ->
            tvTemperatureValue.text = value.toString()
        }

        // Add a listener to the max tokens slider
        sliderMaxTokens.addOnChangeListener { slider, value, fromUser ->
            tvMaxTokensValue.text = value.toInt().toString()
        }
        btnSave.setOnClickListener {
            // EditText에서 값을 가져와 Constants에 저장
            Constants.API_KEY = etApiKey.text.toString()
            Constants.TEMPERATURE = sliderTemperature.value.toFloat()
            Constants.MAX_TOKENS = sliderMaxTokens.value.toInt()
            // 설정 값을 저장하고 종료
            finish()
        }
    }
}