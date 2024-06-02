package com.devvikram.chatmate.models

data class Users(val email: String, val password: String) {

    var username: String = ""
    constructor(email: String, password: String, username: String?) : this(email, password) {
        if (username != null) {
            this.username = username
        }
    }
}


sealed class RegistrationResponse {
    data class Success(val message: String) : RegistrationResponse()
    data class Error(val message: String) : RegistrationResponse()
}

sealed class LoginResponse{
    data class Success(val message: String) : LoginResponse()
    data class Error(val message: String) : LoginResponse()

}