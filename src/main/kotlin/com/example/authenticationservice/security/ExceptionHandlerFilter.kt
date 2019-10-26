package com.example.authenticationservice.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.example.authenticationservice.exceptions.InvalidJwtAuthenticationException
import org.springframework.http.HttpStatus
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletRequest
import org.springframework.web.filter.OncePerRequestFilter

class ExceptionHandlerFilter : OncePerRequestFilter() {
    public override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        try {
            filterChain.doFilter(request, response)
        } catch (e: InvalidJwtAuthenticationException) {
            val errorResponse = ErrorResponse(e.message.orEmpty(), "Authentication Failed!")
            response.status = HttpStatus.UNAUTHORIZED.value()
            response.contentType = "application/json"
            response.writer.write(convertObjectToJson(errorResponse)!!)
        }
    }

    private fun convertObjectToJson(`object`: Any?): String? {
        if (`object` == null) {
            return null
        }
        val mapper = ObjectMapper()
        return mapper.writeValueAsString(`object`)
    }

}

class ErrorResponse(val description: String, val message: String)