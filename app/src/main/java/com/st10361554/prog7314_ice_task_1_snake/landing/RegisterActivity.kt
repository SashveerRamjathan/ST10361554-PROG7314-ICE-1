package com.st10361554.prog7314_ice_task_1_snake.landing

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.st10361554.prog7314_ice_task_1_snake.MainActivity
import com.st10361554.prog7314_ice_task_1_snake.databinding.ActivityRegisterBinding
import com.st10361554.prog7314_ice_task_1_snake.models.User
import kotlinx.coroutines.launch

/**
 * RegisterActivity handles user registration with Firebase Authentication and Firestore.
 * It includes validation, account creation, saving profile data, and navigation to login or main screens.
 */
class RegisterActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityRegisterBinding

    // View Components for user input and actions
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnLogin: Button

    // Firebase Authentication
    private lateinit var auth: FirebaseAuth

    // Firestore Database instance
    private lateinit var firestore: FirebaseFirestore

    /**
     * Called when the activity is created. Sets up UI, Firebase, and listeners.
     */
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        // Inflate layout using view binding
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set window insets for status/navigation bars
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize view components
        etUsername = binding.etUsername
        etEmail = binding.etEmail
        etPassword = binding.etPassword
        etConfirmPassword = binding.etConfirmPassword
        btnRegister = binding.btnRegister
        btnLogin = binding.btnLogin

        // Initialize Firebase Authentication and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setOnClickListeners()
    }

    /**
     * Validates registration fields for completeness, email format, password length, and match.
     * Shows errors on fields if validation fails.
     * @return true if all fields are valid, false otherwise
     */
    private fun validateFields(
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ) : Boolean {
        // Check if all fields are filled
        if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && username.isNotEmpty()) {
            // Check for valid email format ("@" and "." present, and "." after "@")
            if (!email.contains("@") || !email.contains(".")) {
                etEmail.error = "Email is invalid"
                etEmail.requestFocus()
                return false
            }
            if (email.lastIndexOf(".") < email.lastIndexOf("@")) {
                etEmail.error = "Email is invalid"
                etEmail.requestFocus()
                return false
            }
            // Password must be at least 6 characters
            if (password.length < 6) {
                etPassword.error = "Password must be at least 6 characters"
                etPassword.requestFocus()
                return false
            }
            // Password and confirmation must match
            if (password != confirmPassword) {
                etPassword.error = "Passwords do not match"
                etPassword.requestFocus()
                etConfirmPassword.error = "Passwords do not match"
                etConfirmPassword.requestFocus()
                return false
            }
        } else {
            // At least one field is empty
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    /**
     * Sets click listeners for login and register buttons.
     * Handles navigation and user registration logic.
     */
    private fun setOnClickListeners()
    {
        // Navigate to LoginActivity when user presses "Login"
        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Register new user when user presses "Register"
        btnRegister.setOnClickListener {

            // Get input values from fields
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            // Validate all fields first
            val isValid = validateFields(username, email, password, confirmPassword)

            if (isValid)
            {
                // Create user in Firebase Authentication
                registerUser(
                    email = email,
                    password = password,
                    username = username
                )
                { registeredUser ->

                    // If registration succeeded, get user details
                    val userId = registeredUser!!.uid
                    val userEmail = registeredUser.email

                    // Create User model for Firestore
                    val newUser = User(
                        userId = userId,
                        username = username,
                        email = userEmail ?: ""
                    )

                    // Add user to Firestore in background with coroutine
                    lifecycleScope.launch {
                        firestore.collection("users").document(newUser.userId).set(newUser)
                            .addOnSuccessListener {
                                // Show success toast and navigate to MainActivity
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "User Created Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                // Log and show error if Firestore save fails
                                Log.e(
                                    "RegisterActivity",
                                    "Error adding user to FireStore",
                                    e
                                )
                                Toast.makeText(this@RegisterActivity, "Error Saving Profile", Toast.LENGTH_SHORT)
                                    .show()
                                // Restart registration activity on failure
                                val intent = Intent(this@RegisterActivity, RegisterActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                    }
                }
            }
        }
    }

    /**
     * Registers a user in Firebase Authentication and sets their display name.
     * Calls the callback with the FirebaseUser on success, or null on failure.
     */
    private fun registerUser(email: String, password: String, username: String, callback: (FirebaseUser?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Registration successful
                    Toast.makeText(this, "User Registration Successful", Toast.LENGTH_SHORT).show()
                    Log.d("RegisterActivity", "User Registration Successful")

                    // Get current user
                    val user = auth.currentUser

                    // Set displayName to username so it shows in profile
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build()
                    user?.updateProfile(profileUpdates)?.addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            Log.d("RegisterActivity", "User displayName updated")
                        } else {
                            Log.e("RegisterActivity", "Failed to update displayName", updateTask.exception)
                        }
                        callback(user)  // Return user via callback
                    }
                }
                else if (task.exception is FirebaseAuthUserCollisionException) {
                    // Email already in use error
                    Toast.makeText(this, "Email already in use", Toast.LENGTH_SHORT).show()
                    Log.e("RegisterActivity", "Email already in use", task.exception)
                    callback(null)
                }
                else {
                    // Other registration failure
                    Toast.makeText(
                        this,
                        "Registration Failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("RegisterActivity", "Registration Failed", task.exception)
                    callback(null)
                }
            }
            .addOnFailureListener { e ->
                // Registration failed due to other reasons (network, etc)
                Toast.makeText(this, "Registration Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("RegisterActivity", "Registration Failed", e)
                callback(null)
            }
    }
}