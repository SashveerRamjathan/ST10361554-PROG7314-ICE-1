package com.st10361554.prog7314_ice_task_1_snake.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.st10361554.prog7314_ice_task_1_snake.R
import com.st10361554.prog7314_ice_task_1_snake.models.Score
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Adapter for displaying a list of user scores in a RecyclerView.
 * @param scores List of Score objects to display for the user.
 */
class UserScoresAdapter(private val scores: List<Score>) : RecyclerView.Adapter<UserScoresAdapter.ViewHolder>()
{
    /**
     * ViewHolder class that holds and binds views for a single score item.
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // TextView for displaying the score value
        private val tvScore: TextView = itemView.findViewById(R.id.tvScore)
        // TextView for displaying the date of the score
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)

        /**
         * Binds the data from a Score object to the views.
         * @param score The Score object containing the score value and timestamp.
         */
        @SuppressLint("SetTextI18n")
        fun bind(score: Score) {
            // Display score value with "points"
            tvScore.text = "${score.score} points"
            // Convert the timestamp to a human-readable date string
            val date = Date(score.timestamp)
            val sdf = SimpleDateFormat("EEE, d MMMM yyyy", Locale.getDefault())
            val formattedDate = sdf.format(date)
            tvDate.text = formattedDate
        }
    }

    /**
     * Inflates the item view and creates a ViewHolder for a score item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_score, parent, false)
        return ViewHolder(view)
    }

    /**
     * Binds the score data to the ViewHolder at the given position.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(scores[position])
    }

    /**
     * Returns the total number of score items in the list.
     */
    override fun getItemCount(): Int = scores.size
}