package com.devvikram.chatmate.repository

import android.os.Build
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import androidx.annotation.RequiresApi
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.devvikram.chatmate.MyApplication
import com.devvikram.chatmate.SharedPreferencesHelper
import com.devvikram.chatmate.models.Users
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class UserRepositoryImpl(
    private val sharedPreferencesHelper: SharedPreferencesHelper
) : UserRepository {
    private val queue: RequestQueue by lazy {
        Volley.newRequestQueue(MyApplication.context)
    }

    override suspend fun register(users: Users): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val url = "http://192.168.0.104:80/practicephp/insert.php";
            val request = @RequiresApi(Build.VERSION_CODES.R)
            object : StringRequest(
                Method.POST,
                url,
                {

                    try {
                        val jsonObject = JSONObject(it)
                        val status = jsonObject.getBoolean("status")
                        Log.d(TAG, "register:  succsss")
                        continuation.resume(status)
                        if (status) {
                            setRegistered(true)
                        }else{
                            setRegistered(false)
                        }
                    } catch (e: Exception) {
                        continuation.resumeWithException(e)
                    }
                }, {
                    Log.d(TAG, "register: volley error" + it.message)
                    continuation.resumeWithException(it)

                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    val params = HashMap<String, String>()
                    params["username"] = users.username
                    params["email"] = users.email
                    params["password"] = users.password
                    return params
                }

        }
            queue.add(request)
    }

    }

    private fun setRegistered(b: Boolean) {
        sharedPreferencesHelper.setRegistered(b)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override suspend fun login(users: Users): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val url = "http://192.168.0.104:80/practicephp/login.php"
            val request = object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    try {
                        val jsonObject = JSONObject(response)
                        val status = jsonObject.getBoolean("status")
                        Log.d(TAG, "login: success $status")
                        continuation.resume(status) {
                            Log.e(TAG, "Continuation resumed twice: $it")
                        }
                        if (status) {
                            setLoggedIn(true)
                            sharedPreferencesHelper.setUserEmail(users.email)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "JSON parsing error: ${e.message}")
                        if (continuation.isActive) {
                            continuation.resumeWithException(e)
                        }
                    }
                },
                Response.ErrorListener { error ->
                    Log.d(TAG, "login: volley error ${error.message}")
                    if (continuation.isActive) {
                        continuation.resumeWithException(error)
                    }
                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    return hashMapOf(
                        "email" to users.email,
                        "password" to users.password
                    )
                }
            }

            queue.add(request)

            continuation.invokeOnCancellation {
                request.cancel()
            }
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