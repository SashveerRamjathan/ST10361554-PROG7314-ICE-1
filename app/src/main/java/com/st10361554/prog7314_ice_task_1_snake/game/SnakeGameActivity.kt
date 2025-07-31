package com.st10361554.prog7314_ice_task_1_snake.game

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.st10361554.prog7314_ice_task_1_snake.databinding.ActivitySnakeGameBinding
import com.st10361554.prog7314_ice_task_1_snake.models.Score
import java.util.UUID
import kotlin.math.abs

class SnakeGameActivity : AppCompatActivity()
{
    private lateinit var binding: ActivitySnakeGameBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var snakeView: SnakeView

    private var gamePaused = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySnakeGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        snakeView = SnakeView(this)
        binding.gameContainer.addView(snakeView)

        binding.btnPause.setOnClickListener {
            if (!gamePaused) {
                snakeView.pauseGame()
                gamePaused = true
                Toast.makeText(this, "Game Paused", Toast.LENGTH_SHORT).show()
                binding.btnPause.text = "Resume"
            } else {
                snakeView.resumeGame()
                gamePaused = false
                Toast.makeText(this, "Game Resumed", Toast.LENGTH_SHORT).show()
                binding.btnPause.text = "Pause"
            }
        }

        setSwipeListeners()

        snakeView.setGameOverListener { finalScore ->
            showGameOverDialog(finalScore)
            saveScore(finalScore)
        }

        // Live score update
        val updateScoreRunnable = object : Runnable {
            @SuppressLint("SetTextI18n")
            override fun run() {
                binding.scoreChip.text = "Score: ${snakeView.getScore()}"
                binding.scoreChip.postDelayed(this, 200)
            }
        }
        binding.scoreChip.post(updateScoreRunnable)
    }

    // Swipe handling only in game board area
    @SuppressLint("ClickableViewAccessibility")
    private fun setSwipeListeners() {
        var x1 = 0f
        var y1 = 0f
        snakeView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    x1 = event.x
                    y1 = event.y
                }
                MotionEvent.ACTION_UP -> {
                    val x2 = event.x
                    val y2 = event.y
                    val dx = x2 - x1
                    val dy = y2 - y1
                    if (abs(dx) > abs(dy)) {
                        if (dx > 0) snakeView.changeDirection(Direction.RIGHT)
                        else snakeView.changeDirection(Direction.LEFT)
                    } else {
                        if (dy > 0) snakeView.changeDirection(Direction.DOWN)
                        else snakeView.changeDirection(Direction.UP)
                    }
                }
            }
            true
        }
    }

    private fun showGameOverDialog(finalScore: Int) {
        AlertDialog.Builder(this)
            .setTitle("Game Over")
            .setMessage("Your score: $finalScore")
            .setCancelable(false)
            .setPositiveButton("OK") { _, _ -> finish() }
            .show()
    }

    private fun saveScore(score: Int)
    {
        val userId = auth.currentUser?.uid ?: "unknown"

        // get the users username
        val username = auth.currentUser?.displayName ?: "unknown"

        saveScoreToFireStore(userId, username, score)
    }

    private fun saveScoreToFireStore(
        userId: String,
        username: String,
        userScore: Int
    )
    {
        val scoreId = UUID.randomUUID().toString()

        val score = Score(
            userId = userId,
            username = username,
            score = userScore,
            timestamp = System.currentTimeMillis(),
            scoreId = scoreId
        )

        firestore.collection("snake_scores").document(scoreId).set(score)
            .addOnSuccessListener {
                Toast.makeText(this, "Score saved successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving score: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}