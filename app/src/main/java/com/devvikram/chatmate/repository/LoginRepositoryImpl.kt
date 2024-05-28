package com.devvikram.chatmate.repository

import android.os.Build
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import androidx.annotation.RequiresApi
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.devvikram.chatmate.MyApplication
import com.devvikram.chatmate.models.Users
import org.json.JSONObject

class LoginRepositoryImpl : LoginRepository() {
    private val queue : RequestQueue by lazy {
        Volley.newRequestQueue(MyApplication.context)    }
    override suspend fun login(users: Users): Boolean {
        val url = "http://localhost/practicephp/login.php";
        val request = @RequiresApi(Build.VERSION_CODES.R)
        object : StringRequest(
            Method.POST,
            url,
            {
                val jsonObject = JSONObject(it)
                val status = jsonObject.getBoolean("status")
                if (status) {
                    Log.d(TAG, "login: login success")
                } else {
                    Log.d(TAG, "login: login failed")
                }
            },
            {
                Log.d(TAG, "login: volley error" + it.message)

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
        return false;
    }


}