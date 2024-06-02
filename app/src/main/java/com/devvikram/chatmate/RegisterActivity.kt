package com.devvikram.chatmate

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.devvikram.chatmate.databinding.ActivityRegisterBinding
import com.devvikram.chatmate.models.RegistrationResponse
import com.devvikram.chatmate.models.Users

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private  val viewModel: AuthViewModel by viewModels{
        AuthViewModelFactory((application as MyApplication).authRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


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
            val user = Users( email, password,username = username)
            viewModel.registerUser(user)
        }

        viewModel.registrationState.observe(this) {response->
            if (response == null) {
                return@observe
            }
            when (response) {
                is RegistrationResponse.Success ->{
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
                is RegistrationResponse.Error -> {
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }
            }

        }

        binding.navigateToLoginBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }

}