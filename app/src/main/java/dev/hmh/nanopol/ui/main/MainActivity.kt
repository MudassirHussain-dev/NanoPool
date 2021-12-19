package dev.hmh.nanopol.ui.main

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import dev.hmh.nanopol.data.adapter.ViewPagerAdapter
import dev.hmh.nanopol.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity(){

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val arrFragment = arrayOf(
        "Dashboard",
        "Workers",
        "Pool",
        "Payment",
        "Price"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.apply {
            val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
            viewPager.adapter = adapter
            tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = arrFragment[position]
            }.attach()

            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    Log.d("yes", "onTabSelected: ${tab?.text}")
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    Log.d("yes", "onTabSelected: ${tab?.text}")
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    Log.d("yes", "onTabSelected: ${tab?.text}")
                }

            })

        }


    }


}