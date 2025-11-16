package com.ingsis.grupo10.auth.repositories

import com.ingsis.grupo10.auth.models.user.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, String> {
    fun findByEmail(email: String): User?
}
