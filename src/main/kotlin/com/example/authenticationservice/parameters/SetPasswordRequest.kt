package com.example.authenticationservice.parameters

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class SetPasswordRequest(@JsonProperty("email") @field:Email @field:NotBlank val email: String,
                              @JsonProperty("password") @field:NotBlank @field:Size(min = 8, max = 15) var password: String,
                              @JsonProperty("token") @field:NotBlank @field:Size(min = 36, max = 36) var token: String)
