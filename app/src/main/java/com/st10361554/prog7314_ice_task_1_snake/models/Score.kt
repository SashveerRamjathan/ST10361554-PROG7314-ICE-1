package com.st10361554.prog7314_ice_task_1_snake.models

/**
 * Data class representing a user's score record in the Snake game.
 *
 * @property scoreId Unique identifier for the score entry (e.g., Firestore document ID).
 * @property userId Unique identifier for the user who achieved the score.
 * @property username Display name of the user.
 * @property score The score value achieved by the user.
 * @property timestamp The time the score was recorded, in milliseconds since epoch.
 */
data class Score(
    var scoreId: String = "",
    var userId: String = "",
    var username: String = "",
    var score: Int = 0,
    var timestamp: Long = 0L
)