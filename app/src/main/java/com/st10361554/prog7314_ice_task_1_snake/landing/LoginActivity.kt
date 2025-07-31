package com.st10361554.prog7314_ice_task_1_snake.landing

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.st10361554.prog7314_ice_task_1_snake.MainActivity
import com.st10361554.prog7314_ice_task_1_snake.databinding.ActivityLoginBinding

/**
 * LoginActivity handles user authentication with Firebase.
 * It provides UI for email/password input, login, and navigation to registration.
 */
class LoginActivity : AppCompatActivity() {
    // View binding for accessing UI components
    private lateinit var binding: ActivityLoginBinding

    // Firebase Authentication instance
    private lateinit var auth: FirebaseAuth

    // UI components for user input and actions
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button

    /**
     * Called when the activity is starting. Initializes UI and authentication logic.
     */
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        // Inflate the layout using view binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set window insets to avoid overlap with system bars
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Get references to UI components
        etEmail = binding.etEmail
        etPassword = binding.etPassword
        btnLogin = binding.btnLogin
        btnRegister = binding.tvRegister

        // Login button click listener
        btnLogin.setOnClickListener {

            // Get email and password from EditText fields
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Validate email input
            if (email.isEmpty()) {
                etEmail.error = "Email is required"
                etEmail.requestFocus()
                return@setOnClickListener
            }
            // Validate password input
            if (password.isEmpty()) {
                etPassword.error = "Password is required"
                etPassword.requestFocus()
                return@setOnClickListener
            }

            // Attempt to sign in with Firebase Authentication
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Show success message and navigate to MainActivity
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Show error message if login fails
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Register button click listener
        btnRegister.setOnClickListener {
            // Navigate to RegisterActivity for creating a new account
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}