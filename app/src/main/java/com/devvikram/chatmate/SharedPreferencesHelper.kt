package com.devvikram.chatmate

import android.content.Context

class SharedPreferencesHelper(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun setLoggedIn(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("is_logged_in", isLoggedIn)
        editor.apply()
    }
    fun setRegistered(isRegistered: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("is_registered", isRegistered)
        editor.apply()
    }

    fun isRegistered(): Boolean {
        return sharedPreferences.getBoolean("is_registered", false)
    }
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("is_logged_in", false)
    }

    fun setUserEmail(email: String) {
        val editor = sharedPreferences.edit()
        editor.putString("user_email", email)
        editor.apply()
    }

    fun getUserEmail(): String? {
        return sharedPreferences.getString("user_email", null)
    }

    fun clear() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}
