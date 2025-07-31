package com.st10361554.prog7314_ice_task_1_snake.landing

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.st10361554.prog7314_ice_task_1_snake.MainActivity
import com.st10361554.prog7314_ice_task_1_snake.R
import com.st10361554.prog7314_ice_task_1_snake.databinding.ActivitySplashScreenBinding

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity()
{
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Wait for both persistence load AND minimal delay
        Handler(Looper.getMainLooper()).postDelayed({
            checkAuthState()
        }, 2000)
    }

    // Method to check authentication state
    private fun checkAuthState() {
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        val destination =
            if(currentUser!=null)
            {
                MainActivity::class.java
            }else {
                LoginActivity::class.java
            }

        startActivity(Intent(this, destination))
        finish()
    }
}