package com.example.authenticationservice.api

import com.example.authenticationservice.exceptions.ParameterException
import com.example.authenticationservice.service.AuthenticationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import org.springframework.validation.FieldError
import java.util.HashMap
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.http.HttpStatus
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import org.springframework.http.MediaType
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank


@RestController
@RequestMapping("admin")
class AdminAuthenticationController(@Autowired private val authenticationService: AuthenticationService) {

    @GetMapping("/password_reset_token/{email}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getPasswordResetToken(@Valid @Email @NotBlank @PathVariable("email") email: String): String {
        val user = authenticationService.getUserWithEmail(email)
        val mapper = ObjectMapper()
        val rootNode = mapper.createObjectNode()
        (rootNode as ObjectNode).put("name", user.name)
        rootNode.put("email", user.email)
        rootNode.put("is_password_reset_requested", user.isPasswordResetRequested)
        rootNode.put("password_reset_token", user.passwordResetToken)
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode)
    }

    @GetMapping("/email_confirmation_token/{email}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getRegistrationToken(@Valid @Email @NotBlank @PathVariable("email") email: String): String {
        val user = authenticationService.getUserWithEmail(email)
        val mapper = ObjectMapper()
        val rootNode = mapper.createObjectNode()
        (rootNode as ObjectNode).put("name", user.name)
        rootNode.put("email", user.email)
        rootNode.put("is_registration_confirmed", user.isConfirmed)
        rootNode.put("registration_token", user.confirmationToken)
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): Map<String, String> {
        val errors = HashMap<String, String>()
        ex.bindingResult.allErrors.forEach { error ->
            val fieldName = (error as FieldError).field
            val errorMessage = error.getDefaultMessage()
            errors[fieldName] = errorMessage ?: "Error"
        }
        return errors
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ParameterException::class)
    fun handleValidationExceptions(ex: ParameterException): Map<String, String> {
        val errors = HashMap<String, String>()
        errors[ex.parameter] = ex.message;
        return errors
    }
}