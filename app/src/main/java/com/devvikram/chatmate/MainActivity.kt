package com.devvikram.chatmate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.devvikram.chatmate.databinding.ActivityMainBinding
import com.devvikram.chatmate.fragments.UserFragment
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val authViewModel : AuthViewModel by viewModels(){
        AuthViewModelFactory((application as MyApplication).authRepository)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.optionMenuBtn.setOnClickListener {
            handleOptionMenu(binding.optionMenuBtn);
        }


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

    }
    private fun showCustomPopupMenu(view: View) {
        // Inflate the custom layout
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.menu_item_layout, null)

        // Measure the view to get the exact width and height
        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val popupWidth = popupView.measuredWidth

        // Create the PopupWindow with calculated width
        val popupWindow = PopupWindow(
            popupView,
            popupWidth,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        // Set up each menu item click listener
        popupView.findViewById<TextView>(R.id.search_text).setOnClickListener {
            Toast.makeText(this, "Search clicked", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }

        popupView.findViewById<TextView>(R.id.logout_label).setOnClickListener {
            AlertDialog.Builder(this)
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes") { dialog, _ ->
                    logout()
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
            Toast.makeText(this, "Logout clicked", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }

        // Calculate the location for showing the popup window on the right side of the anchor view
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val xOffset = location[0] + view.width
        val yOffset = location[1]

        // Show the popup window at the calculated location
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, xOffset, yOffset)
    }


    private fun handleOptionMenu(view: View) {
        val popUpOption = PopupMenu(this@MainActivity,view)
        val inflater :MenuInflater = popUpOption.menuInflater
        inflater.inflate(R.menu.top_menu,popUpOption.menu)

        popUpOption.setOnMenuItemClickListener { item: MenuItem? ->
            when (item!!.itemId) {
                R.id.search -> {
                    Toast.makeText(this@MainActivity, item.title, Toast.LENGTH_SHORT).show()
                }

                R.id.logout -> {
                    AlertDialog.Builder(this)
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes") { dialog, _ ->
                            logout()
                            dialog.dismiss()
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()
                    Toast.makeText(this@MainActivity, item.title, Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
        popUpOption.show()
    }

    private fun logout() {
        val intent = Intent(this, LoginActivity::class.java)
        authViewModel.logoutUser(this,intent)
    }
}