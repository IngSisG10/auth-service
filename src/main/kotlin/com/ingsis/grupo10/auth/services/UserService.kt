package com.ingsis.grupo10.auth.services

import com.ingsis.grupo10.auth.models.user.User
import com.ingsis.grupo10.auth.models.user.dto.UserResponse
import com.ingsis.grupo10.auth.repositories.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    @Transactional
    fun getOrCreateUser(
        userId: String,
        email: String?,
        name: String?,
    ): UserResponse {
        val user =
            userRepository.findById(userId).orElseGet {
                val newUser =
                    User(
                        id = userId,
                        email = email,
                        name = name,
                    )
                userRepository.save(newUser)
            }

        // Update user info if changed
        var updated = false
        if (email != null && user.email != email) {
            user.email = email
            updated = true
        }
        if (name != null && user.name != name) {
            user.name = name
            updated = true
        }

        if (updated) {
            userRepository.save(user)
        }

        return UserResponse(
            userId = user.id,
            email = user.email,
            name = user.name,
        )
    }

    fun getUserById(userId: String): UserResponse {
        val user =
            userRepository
                .findById(userId)
                .orElseThrow { IllegalArgumentException("User not found") }

        return UserResponse(
            userId = user.id,
            email = user.email,
            name = user.name,
        )
    }

    fun searchUsers(query: String): List<UserResponse> {
        // If query is empty, return empty list (don't return all users)
        if (query.isBlank()) {
            return emptyList()
        }

        val allUsers = userRepository.findAll()
        return allUsers
            .filter { user ->
                user.email?.contains(query, ignoreCase = true) == true ||
                    user.name?.contains(query, ignoreCase = true) == true
            }.map { user ->
                UserResponse(
                    userId = user.id,
                    email = user.email,
                    name = user.name,
                )
            }
    }
}
