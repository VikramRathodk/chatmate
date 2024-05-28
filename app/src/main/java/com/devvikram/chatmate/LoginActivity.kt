package com.devvikram.chatmate

import android.content.Intent
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.devvikram.chatmate.databinding.ActivityLoginBinding
import com.devvikram.chatmate.models.Users
import com.devvikram.chatmate.repository.LoginRepositoryImpl
import com.devvikram.chatmate.repository.LoginUseCaseImpl
import com.devvikram.chatmate.usercases.LoginUserCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var loginUserCase: LoginUserCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginUserCase = LoginUseCaseImpl(LoginRepositoryImpl())



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

        binding.navigateToRegisterBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }


    private fun authenticateUser(email: String, password: String) {
        val user = Users(email, password)
        login(user)
    }

    private fun login(user: Users) {

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val loginSuccess = loginUserCase.login(user)
                Log.d(TAG, "login: login status is _ :: $loginSuccess")
                // Switch back to the main thread
                withContext(Dispatchers.Main) {
                    if (loginSuccess) {
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        binding.loginBtn.setCompleted(true,true)
                    } else {
                        binding.loginBtn.setCompleted(false,true)
                        Toast.makeText(this@LoginActivity, "Login Failed", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Exception occurred " + e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }
}
