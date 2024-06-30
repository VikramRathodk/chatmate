package com.devvikram.chatmate

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.devvikram.chatmate.databinding.ActivityLoginBinding
import com.devvikram.chatmate.models.LoginResponse
import com.devvikram.chatmate.models.Users
import com.google.firebase.firestore.FirebaseFirestore


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory((application as MyApplication).authRepository)
    }
    private val firestore = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val isLoggedIn = authViewModel.isLoggedIn(this)
        if (isLoggedIn) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.loginBtn.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            if (email.isEmpty()) {
                binding.email.error = "Enter your email"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.password.error = "Enter your password"
                return@setOnClickListener
            }

            showLoading(true)
            binding.loginBtn.setCompleted(false, withAnimation = true)
            val user = Users(email, password)
            authViewModel.loginUser(user, this)

        }
        authViewModel.loginState.observe(this) { response ->

            when (response) {
                is LoginResponse.Success -> {
                    showLoading(false)
                    val handler = Handler(Looper.getMainLooper())
                    handler.postDelayed({
                        binding.loginBtn.setCompleted(true, withAnimation = true)
                    }, 1000)
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                is LoginResponse.Error -> {
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.navigateToRegisterBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.loginBtn.isEnabled = !isLoading
        binding.email.isEnabled = !isLoading
        binding.password.isEnabled = !isLoading
    }

}
