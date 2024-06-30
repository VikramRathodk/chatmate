import android.content.Context
import com.devvikram.chatmate.models.Users

class SharedPreference(context: Context) {
    companion object {
        private const val SHARED_PREF_NAME = "user_data"
    }

    private val sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    fun saveUserData(user: Users) {
        editor.putBoolean("isLoggedIn", true)
        editor.putString("email", user.email)
        editor.putString("username", user.username)
        editor.putString("user_id", user._id)
        editor.apply()
    }

    fun getUserEmail(): String? {
        return sharedPreferences.getString("email", "")
    }

    fun getUsername(): String? {
        return sharedPreferences.getString("username", "")
    }
    fun getUid(): String? {
        return sharedPreferences.getString("user_id", "")
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    fun clearUserData() {
        editor.clear().apply()
    }
}
