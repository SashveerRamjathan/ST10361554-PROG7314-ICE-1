package com.st10361554.prog7314_ice_task_1_snake.models

/**
 * Data class representing a user profile in the Snake game application.
 *
 * @property username The display name of the user.
 * @property email The email address of the user.
 * @property userId The unique identifier for the user (e.g., Firebase UID).
 */
data class User(
    val username: String = "",
    val email: String = "",
    val userId: String = ""
)