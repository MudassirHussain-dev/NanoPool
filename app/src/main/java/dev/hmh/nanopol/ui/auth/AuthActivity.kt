package dev.hmh.nanopol.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import dev.hmh.nanopol.databinding.ActivityAuthBinding


@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityAuthBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.hide()

       /* MobileAds.initialize(this) {}


        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)*/
    }
}