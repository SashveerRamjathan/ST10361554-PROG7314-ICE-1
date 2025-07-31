package com.st10361554.prog7314_ice_task_1_snake.scores

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.st10361554.prog7314_ice_task_1_snake.adapters.UserScoresAdapter
import com.st10361554.prog7314_ice_task_1_snake.databinding.ActivityUserScoresBinding
import com.st10361554.prog7314_ice_task_1_snake.models.Score
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Activity for displaying the current user's Snake game scores.
 * Shows scores in a RecyclerView, or a message if no scores exist.
 */
class UserScoresActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityUserScoresBinding

    // UI components for the screen
    private lateinit var tvTitle: TextView          // Title text showing user's name and scores
    private lateinit var rvScores: RecyclerView     // RecyclerView to display scores
    private lateinit var btnBack: Button            // Button to go back to previous screen
    private lateinit var tvNoScores: TextView       // Message shown if there are no scores

    // Firebase authentication and database instances
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    /**
     * Called when the activity is starting.
     * Sets up UI, initializes Firebase, and loads/display user's scores.
     */
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        // Inflate the layout using View Binding
        binding = ActivityUserScoresBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Adjust padding for system bars (status/nav bar)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Reference UI components from binding
        tvTitle = binding.tvTitle
        rvScores = binding.rvScores
        btnBack = binding.btnBack
        tvNoScores = binding.tvNoScores

        // Handle back button click to finish activity
        btnBack.setOnClickListener {
            finish()
        }

        // Load and display user's scores in a coroutine
        lifecycleScope.launch {
            // Get user's display name for title, if available
            val user = auth.currentUser
            val displayName = user?.displayName
            tvTitle.text = "$displayName, Snake Game Scores"

            // Load scores from Firestore and set up the RecyclerView
            val scores = loadUserScores()
            setUpRecyclerView(scores)
        }
    }

    /**
     * Configures the RecyclerView to display a list of scores.
     * If scores are empty, shows a "no scores" message instead.
     */
    private fun setUpRecyclerView(scores: List<Score>)
    {
        if (scores.isNotEmpty())
        {
            rvScores.adapter = UserScoresAdapter(scores)
            rvScores.layoutManager = LinearLayoutManager(this)

            rvScores.visibility = View.VISIBLE
            tvNoScores.visibility = View.GONE
        }
        else
        {
            rvScores.visibility = View.GONE
            tvNoScores.visibility = View.VISIBLE
        }
    }

    /**
     * Loads all scores for the currently authenticated user from Firestore.
     * @return List of Score objects sorted by timestamp descending (most recent first).
     */
    private suspend fun loadUserScores(): List<Score> {
        // Get the current user, or return empty list if not available
        val user = auth.currentUser ?: return emptyList()

        // Get the user's ID for Firestore path
        val userId = user.uid

        return try {
            // Reference to the user's "scores" subcollection in Firestore
            val querySnapshot = firestore.collection("users")
                .document(userId)
                .collection("scores")
                .get()
                .await()

            // Convert Firestore documents to Score objects
            val scoreList = querySnapshot.documents.mapNotNull { it.toObject(Score::class.java) }

            // Sort scores by timestamp (descending) so most recent is first
            scoreList.sortedByDescending { it.timestamp }
        } catch (e: Exception) {
            // If data fetch fails, return empty list
            emptyList()
        }
    }
}