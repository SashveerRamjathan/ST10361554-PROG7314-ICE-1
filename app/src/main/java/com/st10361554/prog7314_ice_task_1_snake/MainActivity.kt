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

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize view components
        btnPlaySnakeGame = binding.btnPlaySnakeGame
        btnUserScores = binding.btnUserScores
        btnOnlineLeaderBoard = binding.btnOnlineLeaderBoard
        btnLogout = binding.btnLogout
        tvUsername = binding.tvUsername

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Set username
        setUsername()

        setOnClickListeners()

    }

    private fun setUsername() {
        val user = auth.currentUser

        if (user != null) {

            tvUsername.text = user.displayName
        }
    }

    private fun setOnClickListeners()
    {
        btnLogout.setOnClickListener {

            // Sign out user
            auth.signOut()

            // Navigate to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnPlaySnakeGame.setOnClickListener {
            val intent = Intent(this, SnakeGameActivity::class.java)
            startActivity(intent)
        }

        btnUserScores.setOnClickListener {
            val intent = Intent(this, UserScoresActivity::class.java)
            startActivity(intent)
        }

        btnOnlineLeaderBoard.setOnClickListener {
            val intent = Intent(this, LeaderBoardActivity::class.java)
            startActivity(intent)
        }
    }
}