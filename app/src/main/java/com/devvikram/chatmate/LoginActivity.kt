package com.devvikram.chatmate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.devvikram.chatmate.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginBtn.setOnClickListener {
            if (binding.email.text.toString().isEmpty()) {
                binding.email.error = "Enter your email"
                return@setOnClickListener
            }
            if (binding.password.text.toString().isEmpty()) {
                binding.password.error = "Enter your password"
                return@setOnClickListener
            }
            authenticateUser(binding.email.text.toString(), binding.password.text.toString())
        }
    }

    private fun authenticateUser(email: String, password: String) {
        TODO("Not yet implemented")
        binding.loginBtn.setCompleted(true, true)

    }
}