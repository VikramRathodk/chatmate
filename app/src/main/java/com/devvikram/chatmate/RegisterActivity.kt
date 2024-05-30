package com.devvikram.chatmate

import android.content.Intent
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.devvikram.chatmate.databinding.ActivityRegisterBinding
import com.devvikram.chatmate.models.Users
import com.devvikram.chatmate.repository.UserRepositoryImpl
import com.devvikram.chatmate.usercases.UseUseCaseImpl
import com.devvikram.chatmate.usercases.UserUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var userCase: UserUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferencesHelper = SharedPreferencesHelper(applicationContext)
        val userRepository = UserRepositoryImpl(sharedPreferencesHelper)
        userCase = UseUseCaseImpl(userRepository)

        binding.registrationBtn.setOnClickListener {
            val username = binding.username.getText().toString();
            val email = binding.email.getText().toString();
            val password = binding.password.getText().toString();

            if (username.isEmpty()) {
                binding.username.error = "Please enter username"
                binding.username.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.password.error = "Please enter password"
                binding.password.requestFocus()
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                binding.email.error = "Please enter email"
                binding.email.requestFocus()
                return@setOnClickListener
            }
            registerUser(username, email, password)
        }

        binding.navigateToLoginBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


    }

    private fun registerUser(username: String, email: String, password: String) {

        val user = Users(username, email, password)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val isRegistered = userCase.register(user)
                Log.d(TAG, "registerUser: $isRegistered")
                if (isRegistered) {
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    binding.email.error = "Email already exists"
                    binding.email.requestFocus()
                    return@launch
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterActivity, e.message, Toast.LENGTH_SHORT).show()
                }

            }


        }

    }
}