package com.devvikram.chatmate.auth.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.devvikram.chatmate.auth.viewmodel.AuthViewModel
import com.devvikram.chatmate.auth.viewmodel.AuthViewModelFactory
import com.devvikram.chatmate.MainActivity
import com.devvikram.chatmate.MyApplication
import com.devvikram.chatmate.databinding.ActivityLoginBinding
import com.devvikram.chatmate.retrofit.model.LoginResponse
import com.devvikram.chatmate.retrofit.model.Users


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory((application as MyApplication).authRepository)
    }


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

            showProgress()
            val user = Users(email, password)
            authViewModel.loginUser(user, this)

        }
        authViewModel.loginState.observe(this) { response ->

            when (response) {
                is LoginResponse.Success -> {

                    hideProgress()
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                is LoginResponse.Error -> {
                    hideProgress()
                    Toast.makeText(this, response.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.navigateToRegisterBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
    private fun showProgress(){
        binding.loginBtn.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }
    private fun hideProgress(){
        binding.loginBtn.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }

}
