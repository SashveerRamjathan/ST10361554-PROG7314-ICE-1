package com.st10361554.prog7314_ice_task_1_snake.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import androidx.recyclerview.widget.RecyclerView
import com.st10361554.prog7314_ice_task_1_snake.R
import com.st10361554.prog7314_ice_task_1_snake.models.Score
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Adapter for displaying a list of scores in a leaderboard within a RecyclerView.
 * @param scores List of Score objects to display.
 */
class LeaderBoardAdapter(private val scores: List<Score>) : RecyclerView.Adapter<LeaderBoardAdapter.ViewHolder>()
{
    /**
     * ViewHolder class that holds references to the views in a leaderboard item layout.
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        // TextView showing the username
        private val tvUsername: TextView = itemView.findViewById(R.id.tvLeaderBoardUsername)
        // TextView showing the score
        private val tvScore: TextView = itemView.findViewById(R.id.tvLeaderboardScore)
        // TextView showing the date
        private val tvDate: TextView = itemView.findViewById(R.id.tvLeaderboardDate)
        // TextView showing the position/rank
        private val tvPosition: TextView = itemView.findViewById(R.id.tvLeaderboardPosition)

        /**
         * Binds the data from a Score object and position to the views.
         * @param score The Score object containing user, score, and timestamp.
         * @param position The position in the leaderboard (used for rank).
         */
        @SuppressLint("SetTextI18n")
        fun bind(score: Score, position: Int) {
            // Set the score with "points" label
            tvScore.text = "${score.score} points"
            // Format the timestamp as a human-readable date
            val date = Date(score.timestamp)
            val sdf = SimpleDateFormat("EEE, d MMMM yyyy", Locale.getDefault())
            val formattedDate = sdf.format(date)
            tvDate.text = formattedDate
            // Set the username
            tvUsername.text = score.username
            // Set the leaderboard position, starting from 1
            tvPosition.text = "#${position + 1}"
        }
    }

    /**
     * Inflates a new leaderboard item view and returns a ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leader_board_score, parent, false)
        return ViewHolder(view)
    }

    /**
     * Binds the score data to the ViewHolder at the given position.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(scores[position], position)
    }

    /**
     * Returns the total number of items in the leaderboard.
     */
    override fun getItemCount(): Int = scores.size
}