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

class RegisterActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityRegisterBinding

    // View Components
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnLogin: Button

    // Firebase Authentication
    private lateinit var auth: FirebaseAuth

    // Firestore Database
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Initialize Firestore Database
        firestore = FirebaseFirestore.getInstance()

        setOnClickListeners()
    }

    private fun validateFields(
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ) : Boolean {
        //check if all fields are filled
        if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && username.isNotEmpty()) {
            // check if email is valid (contains "@" and 1 "." after "@")
            if (!email.contains("@") || !email.contains(".")) {
                etEmail.error = "Email is invalid"
                etEmail.requestFocus()
                return false
            }

            // check if ("." after "@" is not the last character)
            if (email.lastIndexOf(".") < email.lastIndexOf("@")) {
                etEmail.error = "Email is invalid"
                etEmail.requestFocus()
                return false
            }

            // check if password is valid (at least 6 characters)
            if (password.length < 6) {
                etPassword.error = "Password must be at least 6 characters"
                etPassword.requestFocus()
                return false
            }

            // check if password and confirm password match
            if (password != confirmPassword) {
                // show error message in text fields
                etPassword.error = "Passwords do not match"
                etPassword.requestFocus()
                etConfirmPassword.error = "Passwords do not match"
                etConfirmPassword.requestFocus()

                return false
            }
        } else {
            // show error message
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun setOnClickListeners()
    {
        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnRegister.setOnClickListener {

            // get values from fields
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            // validate fields
            val isValid = validateFields(username, email, password, confirmPassword)

            if (isValid)
            {
                // create user in firebase auth
                registerUser(
                    email = email,
                    password = password,
                    username = username
                )
                { registeredUser ->

                    // get the users id
                    val userId = registeredUser!!.uid

                    // get the users email
                    val userEmail = registeredUser.email

                    val newUser = User(
                        userId = userId,
                        username = username,
                        email = userEmail ?: ""
                    )

                    lifecycleScope.launch {
                        // add user to Firebase FireStore
                        firestore.collection("users").document(newUser.userId).set(newUser)

                            .addOnSuccessListener {

                                // display toast
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "User Created Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // navigate to main activity
                                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }

                            .addOnFailureListener { e ->
                                // log the error
                                Log.e(
                                    "RegisterActivity",
                                    "Error adding user to FireStore",
                                    e
                                )

                                // display toast
                                Toast.makeText(this@RegisterActivity, "Error Saving Profile", Toast.LENGTH_SHORT)
                                    .show()

                                // navigate back to register activity
                                val intent = Intent(this@RegisterActivity, RegisterActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                    }
                }
            }

        }
    }

    private fun registerUser(email: String, password: String, username: String, callback: (FirebaseUser?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Registration successful
                    Toast.makeText(this, "User Registration Successful", Toast.LENGTH_SHORT).show()
                    Log.d("RegisterActivity", "User Registration Successful")

                    // Get the currently signed-in user
                    val user = auth.currentUser

                    // Set displayName to username
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
                    // Registration failed due to email already in use
                    Toast.makeText(this, "Email already in use", Toast.LENGTH_SHORT).show()
                    Log.e("RegisterActivity", "Email already in use", task.exception)

                    callback(null)  // Return null if registration fails
                }
                else {
                    // Registration failed
                    Toast.makeText(
                        this,
                        "Registration Failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("RegisterActivity", "Registration Failed", task.exception)

                    callback(null)  // Return null if registration fails
                }
            }
            .addOnFailureListener { e ->
                // Registration failed
                Toast.makeText(this, "Registration Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("RegisterActivity", "Registration Failed", e)

                callback(null)  // Return null if registration fails
            }
    }
}