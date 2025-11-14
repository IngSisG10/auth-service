package com.ingsis.grupo10.auth.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class ServiceApiKeyFilter(
    @Value("\${service.api.key}") private val apiKey: String,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val serviceKey = request.getHeader("X-Service-API-Key")

        if (serviceKey != null && serviceKey == apiKey) {
            val auth =
                UsernamePasswordAuthenticationToken(
                    "service-account",
                    null,
                    listOf(SimpleGrantedAuthority("ROLE_SERVICE")),
                )
            SecurityContextHolder.getContext().authentication = auth
            println("✓ Authenticated as SERVICE for ${request.requestURI}")
        }

        filterChain.doFilter(request, response)
    }
}
