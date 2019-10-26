package com.example.authenticationservice.api

import com.example.authenticationservice.exceptions.ParameterException
import com.example.authenticationservice.parameters.RegisterUserRequest
import com.example.authenticationservice.parameters.PasswordResetRequest
import com.example.authenticationservice.parameters.SetPasswordRequest
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
import com.example.authenticationservice.parameters.AuthenticationRequest
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size


@RestController
@RequestMapping("api")
class AuthenticationController(@Autowired private val authenticationService: AuthenticationService) {

    @PostMapping("/register", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun registerUser(@Valid @RequestBody registerUserRequest: RegisterUserRequest): String {
        authenticationService.registerUser(registerUserRequest.name, registerUserRequest.email, registerUserRequest.password)
        val mapper = ObjectMapper()
        val rootNode = mapper.createObjectNode()
        (rootNode as ObjectNode).put("response", "User created successfully")
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode)
    }

    @GetMapping("/register/{email}/{token}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun confirmUser(@Valid @Email @NotBlank @PathVariable("email") email: String, @Valid @NotBlank @Size(min = 36, max = 36) @PathVariable("token") token: String): String {
        authenticationService.confirmUser(email, token)
        val mapper = ObjectMapper()
        val rootNode = mapper.createObjectNode()
        (rootNode as ObjectNode).put("response", "User confirmed successfully")
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode)
    }

    @PostMapping("/password_reset", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun requestPasswordReset(@Valid @RequestBody passwordResetRequest: PasswordResetRequest): String {
        authenticationService.requestPasswordReset(passwordResetRequest.email)
        val mapper = ObjectMapper()
        val rootNode = mapper.createObjectNode()
        (rootNode as ObjectNode).put("response", "Password change request initiated, please check your email")
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode)
    }

    @PutMapping("/password_reset", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun resetPassword(@Valid @RequestBody setPasswordRequest: SetPasswordRequest): String {
        authenticationService.resetPassword(setPasswordRequest.email, setPasswordRequest.password, setPasswordRequest.token);
        val mapper = ObjectMapper()
        val rootNode = mapper.createObjectNode()
        (rootNode as ObjectNode).put("response", "Password changed successfully")
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode)
    }

    @PostMapping("/login")
    fun login(@RequestBody data: AuthenticationRequest): ResponseEntity<*> {
        val model = authenticationService.login(data.email, data.password)
        return ResponseEntity.ok(model)
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