package com.example.authenticationservice.security

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.config.annotation.SecurityConfigurerAdapter


class JwtConfigurator(private val jwtTokenProvider: JwtTokenProvider) : SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>() {
    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        val customFilter = JwtTokenFilter(jwtTokenProvider)
        val exceptionHandlerFilter = ExceptionHandlerFilter()
        http.addFilterBefore(exceptionHandlerFilter, UsernamePasswordAuthenticationFilter::class.java)
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter::class.java)
    }
}