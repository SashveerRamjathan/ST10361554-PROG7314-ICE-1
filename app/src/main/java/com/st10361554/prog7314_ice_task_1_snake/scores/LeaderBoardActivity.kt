package com.st10361554.prog7314_ice_task_1_snake.scores

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
import com.google.firebase.firestore.FirebaseFirestore
import com.st10361554.prog7314_ice_task_1_snake.adapters.LeaderBoardAdapter
import com.st10361554.prog7314_ice_task_1_snake.databinding.ActivityLeaderBoardBinding
import com.st10361554.prog7314_ice_task_1_snake.models.Score
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Activity that displays the leaderboard with top scores from all users.
 * Loads user scores from Firestore and shows them in a RecyclerView.
 */
class LeaderBoardActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityLeaderBoardBinding

    // UI components
    private lateinit var tvTitle: TextView            // Leaderboard title
    private lateinit var rvScores: RecyclerView       // RecyclerView for displaying scores
    private lateinit var btnBack: Button              // Button to go back to previous screen
    private lateinit var tvNoScores: TextView         // View displayed if no scores exist

    // Firebase Firestore instance for database operations
    private lateinit var firestore: FirebaseFirestore

    /**
     * Called when the activity is created.
     * Sets up the UI, initializes Firestore, and loads leaderboard data.
     */
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        // Inflate layout using View Binding
        binding = ActivityLeaderBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set window insets to fit system bars
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase Firestore
        firestore = FirebaseFirestore.getInstance()

        // Initialize UI components from binding
        tvTitle = binding.tvTitle
        rvScores = binding.rvScores
        btnBack = binding.btnBack
        tvNoScores = binding.tvNoScores

        // Set click listener for Back button to close the leaderboard screen
        btnBack.setOnClickListener {
            finish()
        }

        // Load scores from Firestore and display them in the RecyclerView
        lifecycleScope.launch {
            val scores = loadScores()
            setUpRecyclerView(scores)
        }
    }

    /**
     * Sets up the RecyclerView with the provided scores.
     * If scores list is empty, shows a message instead.
     */
    private fun setUpRecyclerView(scores: List<Score>)
    {
        if (scores.isNotEmpty())
        {
            rvScores.adapter = LeaderBoardAdapter(scores)
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
     * Loads all user scores from Firestore, sorts them by timestamp, and returns the top 10 most recent.
     * @return List of top [Score] objects, or empty list if none found.
     */
    private suspend fun loadScores(): List<Score> {
        return try {
            // Step 1: Get all user documents
            val usersSnapshot = firestore.collection("users").get().await()
            val allScores = mutableListOf<Score>()

            // Step 2: For each user, get their scores subcollection
            for (userDoc in usersSnapshot.documents) {
                val scoresSnapshot = firestore.collection("users")
                    .document(userDoc.id)
                    .collection("scores")
                    .get()
                    .await()

                // Step 3: Map Firestore documents to Score objects and add to list
                val userScores = scoresSnapshot.documents.mapNotNull {
                    it.toObject(Score::class.java)
                }
                allScores.addAll(userScores)
            }

            // Step 4: Sort all scores by timestamp (descending), take top 10
            allScores.sortedByDescending { it.timestamp }.take(10)
        } catch (e: Exception) {
            // If loading fails, print stack trace and return empty list
            e.printStackTrace()
            emptyList()
        }
    }
}