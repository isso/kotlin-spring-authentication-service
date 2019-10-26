package com.example.authenticationservice.exceptions

import org.springframework.security.core.AuthenticationException

class InvalidJwtAuthenticationException(e: String) : AuthenticationException(e)