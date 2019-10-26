package com.example.authenticationservice.dao

import com.example.authenticationservice.model.User
import org.springframework.stereotype.Repository
import java.util.*

@Repository("InMemoryDB")
class UsersDataAccessService : UsersDao {
    private val usersList = ArrayList<User>()

    override fun addUser(user: User) {
        usersList.add(user);
    }

    override fun getUserByEmail(email: String): User? {
        return usersList.filter() { it.email == email }.firstOrNull()
    }

    override fun getAllUsers(): List<User> {
        return usersList
    }

    override fun getAllNotConfirmedUsers(): List<User> {
        return usersList.filter() { !it.isConfirmed }
    }

    override fun getAllConfirmedUsers(): List<User> {
        return usersList.filter() { it.isConfirmed }
    }

    override fun getAllPasswordRequestedUsers(): List<User> {
        return usersList.filter() { it.isPasswordResetRequested }
    }

    override fun userExists(email: String): Boolean {
        return usersList.stream().anyMatch { it.email == email }
    }

}