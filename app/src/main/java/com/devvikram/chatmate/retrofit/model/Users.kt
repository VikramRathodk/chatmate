package com.devvikram.chatmate.retrofit.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.TypeAdapter
import com.google.gson.annotations.JsonAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter


@Entity(tableName = "users")
data class Users(
    val email: String = "",
    val password: String = "",
    @PrimaryKey
    var _id: String = "",
    var username: String = ""
)


sealed class RegistrationResponse {
    data class Success(val message: String) : RegistrationResponse()
    data class Error(val message: String) : RegistrationResponse()
}


@JsonAdapter(LoginResponse.Companion.LoginResponseTypeAdapter::class)
sealed class LoginResponse {
    data class Success(val message: String) : LoginResponse()
    data class Error(val message: String) : LoginResponse()
    companion object {
        class LoginResponseTypeAdapter : TypeAdapter<LoginResponse>() {
            override fun write(out: JsonWriter, value: LoginResponse) {
                throw UnsupportedOperationException("Writing LoginResponse is not supported")
            }

            override fun read(reader: JsonReader): LoginResponse {
                var isSuccess = false
                var errorMessage = ""

                reader.beginObject()
                while (reader.hasNext()) {
                    val name = reader.nextName()
                    when (name) {
                        "status" -> isSuccess = reader.nextBoolean()
                        "message" -> errorMessage = reader.nextString()
                        else -> reader.skipValue()
                    }
                }
                reader.endObject()

                return if (isSuccess) {
                    Success(errorMessage)
                } else {
                    Error(errorMessage)
                }
            }
        }
    }
}
