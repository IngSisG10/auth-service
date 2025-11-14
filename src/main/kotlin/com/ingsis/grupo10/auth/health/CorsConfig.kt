package com.ingsis.grupo10.auth.health

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class CorsConfig {
    @Value("\${cors.allowed-origins:http://localhost:3000}")
    private lateinit var allowedOrigins: String

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()

        // Parse comma-separated allowed origins
        configuration.allowedOrigins = allowedOrigins.split(",").map { it.trim() }

        // Allow common HTTP methods
        configuration.allowedMethods =
            listOf(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "PATCH",
                "OPTIONS",
            )

        // Allow common headers
        configuration.allowedHeaders =
            listOf(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "X-Requested-With",
            )

        // Allow credentials (cookies, authorization headers)
        configuration.allowCredentials = true

        // Cache preflight response for 1 hour
        configuration.maxAge = 3600L

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}
