package dev.hmh.nanopol.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint
import dev.hmh.nanopol.R
import dev.hmh.nanopol.databinding.ActivityAuthBinding
import dev.hmh.nanopol.databinding.ActivityNewAccountBinding

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