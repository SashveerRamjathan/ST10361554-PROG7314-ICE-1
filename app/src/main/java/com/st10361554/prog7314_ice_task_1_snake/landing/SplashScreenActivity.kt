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

/**
 * SplashScreenActivity displays a splash screen for a fixed duration.
 * After the delay, it checks the user's authentication state and navigates to the appropriate screen.
 */
@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity()
{
    // View binding object for accessing UI elements in the splash screen layout
    private lateinit var binding: ActivitySplashScreenBinding
    // Firebase Authentication instance
    private lateinit var auth: FirebaseAuth

    /**
     * Called when the activity is starting.
     * Sets up the splash screen UI and schedules a check for authentication state after a delay.
     */
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Enables drawing behind system bars for a modern look

        // Inflate the splash screen layout using view binding
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Adjust the padding of the root view to avoid system bar overlap
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Wait for a minimal delay (2 seconds) to show the splash screen, then check authentication state
        Handler(Looper.getMainLooper()).postDelayed({
            checkAuthState()
        }, 2000)
    }

    /**
     * Checks if a user is currently authenticated.
     * Navigates to MainActivity if authenticated, otherwise to LoginActivity.
     */
    private fun checkAuthState() {
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        // Determine which activity to start based on authentication state
        val destination =
            if(currentUser != null)
            {
                MainActivity::class.java // User is authenticated
            } else {
                LoginActivity::class.java // User is not authenticated
            }

        // Start the determined activity and close the splash screen
        startActivity(Intent(this, destination))
        finish()
    }
}