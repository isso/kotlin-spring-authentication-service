package com.example.authenticationservice.service

import com.example.authenticationservice.security.JwtTokenProvider
import com.example.authenticationservice.dao.UsersDao
import com.example.authenticationservice.exceptions.ParameterException
import com.example.authenticationservice.model.User
import org.mindrot.jbcrypt.BCrypt
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthenticationService(@Autowired @Qualifier("InMemoryDB") private val usersDao: UsersDao) {

    @Autowired
    internal var jwtTokenProvider: JwtTokenProvider? = null

    fun registerUser(name: String, email: String, password: String) {
        if (usersDao.userExists(email)) throw ParameterException("user", "User already exists");
        val token = UUID.randomUUID().toString();
        usersDao.addUser(User(name, email, BCrypt.hashpw(password, BCrypt.gensalt()), token))
    }

    fun confirmUser(email: String, token: String) {
        val user = usersDao.getUserByEmail(email) ?: throw ParameterException("user", "User doesn't exist")
        if (user.isConfirmed) throw ParameterException("user", "User already confirmed")
        if (user.confirmationToken != token) throw ParameterException("token", "Incorrect confirmation token")
        user.isConfirmed = true
    }

    fun requestPasswordReset(email: String) {
        val user = usersDao.getUserByEmail(email) ?: throw ParameterException("user", "User doesn't exist")
        if (!user.isConfirmed) throw ParameterException("user", "User isn't confirmed")
        user.isPasswordResetRequested = true
        user.passwordResetToken = UUID.randomUUID().toString()
    }

    fun resetPassword(email: String, password: String, token: String) {
        val user = usersDao.getUserByEmail(email) ?: throw ParameterException("user", "User doesn't exist")
        if (!user.isConfirmed) throw ParameterException("user", "User isn't confirmed")
        if (!user.isPasswordResetRequested) throw ParameterException("password", "Password change hasn't been requested")
        if (user.passwordResetToken != token) throw ParameterException("password", "Incorrect password change token")
        user.isPasswordResetRequested = false
        user.passwordResetToken = ""
        user.password = password;
    }

    fun getUserWithEmail(email: String): User {
        return usersDao.getUserByEmail(email) ?: throw ParameterException("user", "User doesn't exist")
    }

    fun login(email: String, password: String): HashMap<Any, Any> {
        val user = usersDao.getUserByEmail(email) ?: throw ParameterException("user", "User doesn't exist")
        if (!user.isConfirmed) throw ParameterException("user", "User isn't confirmed")
        if (!BCrypt.checkpw(password, user.password)) throw BadCredentialsException("Invalid username/password supplied")
        val token = jwtTokenProvider!!.createToken(email)
        val model = HashMap<Any, Any>()
        model["email"] = email
        model["token"] = token
        return model
    }
}