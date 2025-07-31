package com.st10361554.prog7314_ice_task_1_snake

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.st10361554.prog7314_ice_task_1_snake.databinding.ActivityMainBinding
import com.st10361554.prog7314_ice_task_1_snake.game.SnakeGameActivity
import com.st10361554.prog7314_ice_task_1_snake.landing.LoginActivity
import com.st10361554.prog7314_ice_task_1_snake.scores.LeaderBoardActivity
import com.st10361554.prog7314_ice_task_1_snake.scores.UserScoresActivity

class MainActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityMainBinding

    // View Components
    private lateinit var btnPlaySnakeGame: Button
    private lateinit var btnUserScores: Button
    private lateinit var btnOnlineLeaderBoard: Button
    private lateinit var btnLogout: Button
    private lateinit var tvUsername: TextView

    // Firebase Authentication
    private lateinit var auth: FirebaseAuth

    // Called when the activity is starting
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        // Inflate the layout for this activity using View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set window insets to ensure UI fits system bars (status/navigation)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize view components from the binding
        btnPlaySnakeGame = binding.btnPlaySnakeGame
        btnUserScores = binding.btnUserScores
        btnOnlineLeaderBoard = binding.btnOnlineLeaderBoard
        btnLogout = binding.btnLogout
        tvUsername = binding.tvUsername

        // Initialize Firebase Authentication instance
        auth = FirebaseAuth.getInstance()

        // Set the username in the TextView if user is logged in
        setUsername()

        // Set up click listeners for all buttons
        setOnClickListeners()
    }

    // Sets the TextView to display the current user's display name, if logged in
    private fun setUsername() {
        val user = auth.currentUser
        if (user != null) {
            tvUsername.text = user.displayName
        }
    }

    // Sets up click listeners for all buttons on the main screen
    private fun setOnClickListeners()
    {
        // Logout button: signs out user and navigates to LoginActivity
        btnLogout.setOnClickListener {
            // Sign out user from Firebase
            auth.signOut()
            // Navigate to LoginActivity and finish this activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Play Snake Game button: starts the SnakeGameActivity
        btnPlaySnakeGame.setOnClickListener {
            val intent = Intent(this, SnakeGameActivity::class.java)
            startActivity(intent)
        }

        // User Scores button: starts the UserScoresActivity
        btnUserScores.setOnClickListener {
            val intent = Intent(this, UserScoresActivity::class.java)
            startActivity(intent)
        }

        // Online LeaderBoard button: starts the LeaderBoardActivity
        btnOnlineLeaderBoard.setOnClickListener {
            val intent = Intent(this, LeaderBoardActivity::class.java)
            startActivity(intent)
        }
    }
}