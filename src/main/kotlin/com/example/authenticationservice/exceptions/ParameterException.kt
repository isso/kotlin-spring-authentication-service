package com.example.authenticationservice.exceptions

import java.lang.RuntimeException

class ParameterException(val parameter: String, override val message: String) : RuntimeException(message) {
}