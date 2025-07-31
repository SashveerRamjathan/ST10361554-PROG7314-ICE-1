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

class UserScoresActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityUserScoresBinding

    // View Components
    private lateinit var tvTitle: TextView
    private lateinit var rvScores: RecyclerView
    private lateinit var btnBack: Button
    private lateinit var tvNoScores: TextView

    private lateinit var auth: FirebaseAuth

    private lateinit var firestore: FirebaseFirestore

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        binding = ActivityUserScoresBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

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

            // get users display name
            val user = auth.currentUser
            val displayName = user?.displayName
            tvTitle.text = "$displayName, Snake Game Scores"

            val scores = loadUserScores()
            setUpRecyclerView(scores)
        }

    }

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

    private suspend fun loadUserScores(): List<Score> {
        // Get the current user
        val user = auth.currentUser ?: return emptyList()

        // Get the user's ID
        val userId = user.uid

        return try {
            // Reference to the user's "scores" subcollection
            val querySnapshot = firestore.collection("users")
                .document(userId)
                .collection("scores")
                .get()
                .await()

            // Convert the results to a list of Score objects
            val scoreList = querySnapshot.documents.mapNotNull { it.toObject(Score::class.java) }

            // Sort the list of scores by timestamp in descending order
            scoreList.sortedByDescending { it.timestamp }
        } catch (e: Exception) {
            // If fetch failed, return empty list
            emptyList()
        }
    }
}