package com.ingsis.grupo10.auth.repositories

import com.ingsis.grupo10.auth.models.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UserRepository : JpaRepository<User, String> {
    fun findByEmail(email: String): User?

    @Query("SELECT u FROM User u WHERE u.id != :userId AND (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))")
    fun searchUsers(
        userId: String,
        email: String?,
        pageable: Pageable,
    ): Page<User>

    @Query("SELECT COUNT(u) FROM User u WHERE u.id != :userId AND (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))")
    fun countSearchUsers(
        userId: String,
        email: String?,
    ): Long
}
