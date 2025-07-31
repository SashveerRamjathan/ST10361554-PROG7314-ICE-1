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

class LeaderBoardAdapter(private val scores: List<Score>) : RecyclerView.Adapter<LeaderBoardAdapter.ViewHolder>()
{
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        private val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        private val tvScore: TextView = itemView.findViewById(R.id.tvLeaderboardScore)
        private val tvDate: TextView = itemView.findViewById(R.id.tvLeaderboardDate)

        @SuppressLint("SetTextI18n")//
        fun bind(score: Score) {
            tvScore.text = "${score.score} points"
            val date = Date(score.timestamp)
            val sdf = SimpleDateFormat("EEE, d MMMM yyyy", Locale.getDefault())
            val formattedDate = sdf.format(date)
            tvDate.text = formattedDate
            tvUsername.text = score.username
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leader_board_score, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(scores[position])
    }

    override fun getItemCount(): Int = scores.size
}