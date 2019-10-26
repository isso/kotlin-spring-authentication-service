package com.example.authenticationservice.parameters

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class PasswordResetRequest(@JsonProperty("email") @field:Email @field:NotBlank val email: String)