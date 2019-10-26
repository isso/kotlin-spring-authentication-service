package com.example.authenticationservice.parameters

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class ConfirmUserRequest(@JsonProperty("email") @field:Email @field:NotBlank val email: String,
                              @JsonProperty("token") @field:NotBlank @field:Size(min = 36, max = 36) var token: String)