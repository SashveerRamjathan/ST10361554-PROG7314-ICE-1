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

class LeaderBoardActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityLeaderBoardBinding

    // View Components
    private lateinit var tvTitle: TextView
    private lateinit var rvScores: RecyclerView
    private lateinit var btnBack: Button
    private lateinit var tvNoScores: TextView

    private lateinit var firestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        binding = ActivityLeaderBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase Firestore
        firestore = FirebaseFirestore.getInstance()

        // Initialize View Components
        tvTitle = binding.tvTitle
        rvScores = binding.rvScores
        btnBack = binding.btnBack
        tvNoScores = binding.tvNoScores

        // Set click listener for Back button
        btnBack.setOnClickListener {
            finish()
        }

        lifecycleScope.launch {

            val scores = loadScores()
            setUpRecyclerView(scores)
        }
    }

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

    private suspend fun loadScores(): List<Score> {
        return try {
            // Step 1: Get all users
            val usersSnapshot = firestore.collection("users").get().await()
            val allScores = mutableListOf<Score>()

            // Step 2: For each user, get their scores subcollection
            for (userDoc in usersSnapshot.documents) {
                val scoresSnapshot = firestore.collection("users")
                    .document(userDoc.id)
                    .collection("scores")
                    .get()
                    .await()

                // Step 3: Add all scores to the list
                val userScores = scoresSnapshot.documents.mapNotNull {
                    it.toObject(Score::class.java)
                }
                allScores.addAll(userScores)
            }

            // Step 4: Sort and take top 10
            allScores.sortedByDescending { it.timestamp }.take(10)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}