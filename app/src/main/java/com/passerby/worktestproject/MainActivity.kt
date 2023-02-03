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
import androidx.recyclerview.widget.LinearLayoutManager
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
    private var mainList: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferences = getSharedPreferences("APP_PREFERENCES", MODE_PRIVATE)
        setOnBackPressedDispatcher()
        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[MainViewModel::class.java]

        createList()

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
                val rvAdapter = MainRVAdapter(mainList)
                binding.recyclerView.apply {
                    visibility = View.VISIBLE
                    layoutManager =
                        LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
                    adapter = rvAdapter
                }
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

    fun createList() {
        mainList.addAll(
            listOf(
                "1. Badminton is the fastest racket sport: the speed of a shuttlecock can reach an average of 270 km/h.",
                "2. If you're bowling, don't try to knock down the pins as hard as you can. This is a very tricky moment, because a bowling pin for a fall requires a deflection of only 7.5 degrees.",
                "3. Boxing was only legalized as a sport in 1900. Prior to that, he was considered too cruel and unsuitable for the presence of a possible public. In the 20th century, boxing became the most popular sport in cinema.",
                "4. Ancient Greek Olympic athletes competed completely naked. All Olympic competitions provided for the complete nakedness of athletes. The very name of the modern word \"gymnastics\" comes from the ancient Greek \"gymos\", that is, \"naked\". Somehow, they still tried to dress the athletes, but this innovation did not take root at all.",
                "5. Quite remarkable, but the first puck for playing hockey was, you will be surprised - a square shape! There was a certain period when hockey was played with round wooden pucks. A modern hockey puck is made of vulcanized rubber and weighs only 200 grams. Before the start of the game, it is frozen so that it does not spring.",
                "6. Did you know? That the first products of the Dassler family, the founders of Adidas, were sleeping slippers.",
                "7. The fastest man in the world - Usain Bolt (Jamaica). Jamaican track and field athlete, specializing in sprint, nine-time Olympic champion and 11-time world champion (a record in the history of these competitions). During his performances, he set 8 world records. World record holder in the 100 - 9.58; and 200 meters - 19.19, as well as in the 4 × 100 meters relay as part of the Jamaican national team - 36.84.",
                "8. In the Spanish Second League match between San Isidro and Olimpico Carrante, a few minutes before the end of the game, the players of the two teams, completely dissatisfied with the refereeing, surrounded the referee to explain to him how to referee. And not only with words and gestures. In this critical situation, the referee, keeping complete calm, took out a red card and presented it to all twenty-two participants in the match.",
                "9. Athletes jumping from a springboard on skis should not be wished for a fair wind - it only harms them. A headwind is much better, thanks to which an air cushion is created in front of the skier in flight, and he flies further. To start the jump, athletes are given a certain time, during which the coaches try to choose the optimal start time, taking into account the wind. Wind changes during the course of the competition can make the conditions for the participants unequal: if the skier got only a tailwind, his chances for medals, even with the best technique, are sharply reduced.",
                "10. The rules of horse racing state that the length of the name of a racing horse should not exceed eighteen letters. Names that are too long are cumbersome to record.",
                "11. There are only 336 notches in a standard golf ball.",
                "12. In the football championship of the State of the Vatican such teams as \"Telemail\", \"Guards\", \"Bank\", \"Library\", \"Team of Museums\" play.",
                "13. The FIVB rules prohibit holding classic volleyball competitions at indoor temperatures above +25 and below +16, but there are no temperature restrictions for beach volleyball.",
                "14. People began to play bowling as early as 3200 BC, this is evidenced by a collection of objects found in an Egyptian tomb that resemble primitive bowling tools.",
                "15. The billiard game of snooker fell into decline in the mid-20th century. However, interest in her again greatly increased after the BBC channel chose her to demonstrate the benefits of color television and began to broadcast all the championships. The green table and multi-colored snooker balls were perfect for this purpose."
            )
        )
    }
}