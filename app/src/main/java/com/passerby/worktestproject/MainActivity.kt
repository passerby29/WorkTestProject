package com.passerby.worktestproject

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.passerby.worktestproject.databinding.ActivityMainBinding
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferences: SharedPreferences
    private lateinit var viewModel: MainViewModel
    lateinit var url: String
    private val placeholder = "https://fitostrov.ru/25-faktov-o-sporte/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferences = getSharedPreferences("APP_PREFERENCES", MODE_PRIVATE)
        setOnBackPressedDispatcher()
        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[MainViewModel::class.java]

        val flag = preferences.contains("url")

        if (viewModel.checkForInternet(this)) {
            val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = 0
            }
            remoteConfig.setConfigSettingsAsync(configSettings)
            remoteConfig.fetchAndActivate().addOnCompleteListener(this) { task ->
                if (task.isComplete) {
                    Log.d("Firebase", "Success")
                } else {
                    Log.d("Firebase", "Failed")
                }
            }
            url = Firebase.remoteConfig.getString("url")
            if (url.isEmpty() || !checkIsSimAvailable() || viewModel.checkIsEmu()) {
                openWebView(placeholder)
            } else {
                openWebView(url)
                val editor = preferences.edit()
                editor.putString("url", url)
                editor.apply()
            }
        } else {
            binding.noInternetTv.visibility = View.VISIBLE
        }
    }

    private fun setOnBackPressedDispatcher() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.mainWebView.canGoBack()) binding.mainWebView.goBack()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    fun checkIsSimAvailable(): Boolean {
        val tm = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        return when (tm.simState) {
            TelephonyManager.SIM_STATE_ABSENT -> false
            else -> {
                true
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun openWebView(url: String) {
        binding.mainWebView.webViewClient = WebViewClient()
        binding.mainWebView.settings.javaScriptEnabled = true
        binding.mainWebView.settings.domStorageEnabled = true
        binding.mainWebView.settings.javaScriptCanOpenWindowsAutomatically = true
        binding.mainWebView.loadUrl(url)
    }
}