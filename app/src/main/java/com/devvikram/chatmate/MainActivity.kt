package com.devvikram.chatmate

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.devvikram.chatmate.databinding.ActivityMainBinding
import com.devvikram.chatmate.repository.LoginRepositoryImpl
import com.devvikram.chatmate.usercases.LoginUseCaseImpl
import com.devvikram.chatmate.usercases.LoginUserCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var loginUserCase: LoginUserCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPreferencesHelper = SharedPreferencesHelper(applicationContext)
        val userRepository = LoginRepositoryImpl(sharedPreferencesHelper)
        loginUserCase = LoginUseCaseImpl(userRepository)


        binding.logout.setOnClickListener {
            AlertDialog.Builder(this)
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes") { dialog, _ ->
                    logout()
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .create().show()

        }
    }

    private fun logout() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                loginUserCase.logout()
                withContext(Dispatchers.Main) {
//                    finish() this actiity so that it goes to login page
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                    Toast.makeText(this@MainActivity, "Logged Out", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, e.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}