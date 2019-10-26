package com.example.authenticationservice.model

data class User(val name: String, val email: String, var password: String, val confirmationToken: String) {
    var isConfirmed = false
    var isPasswordResetRequested = false
    var passwordResetToken = ""
}