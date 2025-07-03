package com.example.match_mate

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.match_mate.databinding.ActivityMainBinding
import com.example.match_mate.ui.accepted.AcceptedFragment
import com.example.match_mate.ui.declined.DeclinedFragment
import com.example.match_mate.ui.home.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlin.math.max

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyWindowInsets()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, HomeFragment())
                .commit()
        }

        binding.bottomNavigationView.setOnItemSelectedListener {
            val selectedFragment: Fragment = when (it.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_accepted -> AcceptedFragment()
                R.id.nav_declined -> DeclinedFragment()
                else -> HomeFragment()
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, selectedFragment)
                .commit()

            true
        }
    }

    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.mainContainer) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }
    }
}