package com.example.authenticationservice.security

import com.example.authenticationservice.dao.UsersDao
import com.example.authenticationservice.exceptions.InvalidJwtAuthenticationException
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys.secretKeyFor
import javax.servlet.http.HttpServletRequest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import javax.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.*
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User


@Component
class JwtTokenProvider {
    @Value("\${security.jwt.token.secret-key:secret}")
    private lateinit var secretKey: String
    @Value("\${security.jwt.token.expire-length:3600000}")
    private val validityInMilliseconds: Long = 3600000 // 1h
    @Autowired
    private val usersDao: UsersDao? = null

    @PostConstruct
    protected fun init() {
        secretKey = Base64.getEncoder().encodeToString(secretKeyFor(SignatureAlgorithm.HS256).toString().toByteArray())
    }

    fun createToken(username: String): String {
        val claims = Jwts.claims().setSubject(username)
        val now = Date()
        val validity = Date(now.getTime() + validityInMilliseconds)
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)//
                .compact()
    }

    fun getAuthentication(token: String): Authentication {
        val email = this.usersDao!!.getUserByEmail(getEmail(token))?.email
        val jwsClaims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
        val claims = jwsClaims.body
        val scopesString = claims["scopes"].toString()
        val authStrings = scopesString.split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()

        val authorities = authStrings.map { SimpleGrantedAuthority(it) }
        val principal = User(email, "", authorities)
        return UsernamePasswordAuthenticationToken(principal, null, authorities)
    }

    fun getEmail(token: String): String {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).body.subject
    }

    fun resolveToken(req: HttpServletRequest): String? {
        val bearerToken = req.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7, bearerToken.length)
        } else null
    }

    fun validateToken(token: String): Boolean {
        try {
            val claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
            return !claims.body.expiration.before(Date())
        } catch (e: JwtException) {
            throw InvalidJwtAuthenticationException("Expired or invalid JWT token")
        } catch (e: IllegalArgumentException) {
            throw InvalidJwtAuthenticationException("Expired or invalid JWT token")
        }

    }
}