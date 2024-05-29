package com.devvikram.chatmate.repository

import android.os.Build
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import androidx.annotation.RequiresApi
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.devvikram.chatmate.MyApplication
import com.devvikram.chatmate.SharedPreferencesHelper
import com.devvikram.chatmate.models.Users
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LoginRepositoryImpl(
    private val sharedPreferencesHelper: SharedPreferencesHelper
) : LoginRepository {
    private val queue: RequestQueue by lazy {
        Volley.newRequestQueue(MyApplication.context)
    }

    override suspend fun login(users: Users): Boolean {

        return suspendCancellableCoroutine { continuation ->
            val url = "http://192.168.0.104:80/practicephp/login.php";
            val request = @RequiresApi(Build.VERSION_CODES.R)
            object : StringRequest(
                Method.POST,
                url,
                {
                    try {
                        val jsonObject = JSONObject(it)
                        val status = jsonObject.getBoolean("status")
                        Log.d(TAG, "login:  succsss")
                        continuation.resume(status)
                        if (status) {
                            setLoggedIn(true)
                            sharedPreferencesHelper.setUserEmail(users.email)
                        }
                    } catch (e: Exception) {
                        continuation.resumeWithException(e)
                    }


                },
                {
                    Log.d(TAG, "login: volley error" + it.message)
                    continuation.resumeWithException(it)

                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    val params = HashMap<String, String>()
                    params["email"] = users.email
                    params["password"] = users.password
                    return params
                }
            }
            queue.add(request)
        }


    }

    override fun setLoggedIn(isLoggedIn: Boolean) {
        sharedPreferencesHelper.setLoggedIn(isLoggedIn)
    }

    override fun isLoggedIn(): Boolean {
        return sharedPreferencesHelper.isLoggedIn()
    }


    override fun setUserEmail(email: String) {
        sharedPreferencesHelper.setUserEmail(email)
    }

    override fun getUserEmail(): String? {
        return sharedPreferencesHelper.getUserEmail()
    }

    override fun clear() {
        sharedPreferencesHelper.clear()
    }

    override fun logout() {
        setLoggedIn(false)
        clear()
    }


}