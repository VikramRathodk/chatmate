package com.devvikram.chatmate

import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.devvikram.chatmate.databinding.ActivityMainBinding
import com.devvikram.chatmate.fragments.UserFragment
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val firestore = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, UserFragment()).commit()
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.chat_item -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, UserFragment()).commit()
                }
                R.id.updates_item -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, UserFragment()).commit()
                }
                R.id.more_item -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, UserFragment()).commit()
                }
                else -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, UserFragment()).commit()
                }
                }
            true
            }


        firestore.collection("conversation").get().addOnSuccessListener {
            val list = it.documents
            list.forEach {
                val message = it.getString("msg")
                Log.d(TAG, "onCreate: $message")
            }
            
        }.addOnFailureListener {

        }

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

    }
}