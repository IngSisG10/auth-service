package com.ingsis.grupo10.auth.repositories

import com.ingsis.grupo10.auth.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID>
