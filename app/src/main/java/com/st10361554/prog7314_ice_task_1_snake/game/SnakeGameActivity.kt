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

/**
 * Activity for the Snake Game screen.
 * Handles the game lifecycle, user interactions, score management, and persistence.
 */
class SnakeGameActivity : AppCompatActivity()
{
    // View binding for accessing UI components
    private lateinit var binding: ActivitySnakeGameBinding
    // Firebase Authentication instance
    private lateinit var auth: FirebaseAuth
    // Firebase Firestore instance for saving scores
    private lateinit var firestore: FirebaseFirestore

    // Custom SnakeView that renders and manages the game logic
    private lateinit var snakeView: SnakeView

    // Indicates if the game is currently paused
    private var gamePaused = false

    /**
     * Called when the activity is created.
     * Initializes views, Firebase, the game, listeners, and live score updates.
     */
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate and set the layout using view binding
        binding = ActivitySnakeGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase authentication and database
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Add the SnakeView (game board) to the container
        snakeView = SnakeView(this)
        binding.gameContainer.addView(snakeView)

        // Pause/Resume button toggles game state and updates UI
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

        // Set up swipe controls for the game
        setSwipeListeners()

        // Set the game over listener to show dialog and save score
        snakeView.setGameOverListener { finalScore ->
            showGameOverDialog(finalScore)
            saveScore(finalScore)
        }

        // Update the score chip every 200ms with the current score
        val updateScoreRunnable = object : Runnable {
            @SuppressLint("SetTextI18n")
            override fun run() {
                binding.scoreChip.text = "Score: ${snakeView.getScore()}"
                binding.scoreChip.postDelayed(this, 200)
            }
        }
        binding.scoreChip.post(updateScoreRunnable)
    }

    /**
     * Sets swipe listeners on the game board to control snake direction.
     * Only responds to swipe gestures (left, right, up, down).
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setSwipeListeners() {
        var x1 = 0f
        var y1 = 0f
        snakeView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Record starting touch position
                    x1 = event.x
                    y1 = event.y
                }
                MotionEvent.ACTION_UP -> {
                    // Calculate movement delta on release
                    val x2 = event.x
                    val y2 = event.y
                    val dx = x2 - x1
                    val dy = y2 - y1
                    // Determine direction based on greater movement axis
                    if (abs(dx) > abs(dy)) {
                        if (dx > 0) snakeView.changeDirection(Direction.RIGHT)
                        else snakeView.changeDirection(Direction.LEFT)
                    } else {
                        if (dy > 0) snakeView.changeDirection(Direction.DOWN)
                        else snakeView.changeDirection(Direction.UP)
                    }
                }
            }
            true // consume event
        }
    }

    /**
     * Shows a modal dialog when the game is over, displaying the user's score.
     * The dialog cannot be cancelled and closes the activity after acknowledgment.
     */
    private fun showGameOverDialog(finalScore: Int) {
        AlertDialog.Builder(this)
            .setTitle("Game Over")
            .setMessage("Your score: $finalScore")
            .setCancelable(false)
            .setPositiveButton("OK") { _, _ -> finish() }
            .show()
    }

    /**
     * Retrieves the current user's ID and username then saves the score to Firestore.
     * @param score The score achieved in the game.
     */
    private fun saveScore(score: Int)
    {
        val userId = auth.currentUser?.uid ?: "unknown"
        // Get the user's username, or "unknown" if not available
        val username = auth.currentUser?.displayName ?: "unknown"

        saveScoreToFireStore(userId, username, score)
    }

    /**
     * Persists the score to Firestore under the user's scores collection.
     * @param userId The user's unique ID.
     * @param username The user's display name.
     * @param userScore The score to record.
     */
    private fun saveScoreToFireStore(
        userId: String,
        username: String,
        userScore: Int
    )
    {
        // Generate a unique score ID
        val scoreId = UUID.randomUUID().toString()

        // Create a Score object with relevant information
        val score = Score(
            userId = userId,
            username = username,
            score = userScore,
            timestamp = System.currentTimeMillis(),
            scoreId = scoreId
        )

        // Save the score to Firestore in users/{userId}/scores/{scoreId}
        firestore.collection("users")
            .document(userId)
            .collection("scores")
            .document(scoreId)
            .set(score)
            .addOnSuccessListener {
                Toast.makeText(this, "Score saved successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving score: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}