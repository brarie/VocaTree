package com.monovoca.server.auth.infrastructure

import com.monovoca.server.auth.domain.port.AccessTokenProvider
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JWTAuthFilter(
    private val accessTokenProvider: AccessTokenProvider
) : OncePerRequestFilter() {

    private val klogger = KotlinLogging.logger {}

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val accessToken = resolveToken(request)
            if (accessToken != null) {
                val authToken = accessTokenProvider.parse(accessToken)
                val principal = UserPrincipal(
                    id = authToken.userId,
                    role = authToken.role
                )
                val authentication = UsernamePasswordAuthenticationToken(
                    principal,
                    null,
                    principal.authorities
                )
                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (e: Exception) {
            klogger.info(e) { "JWT 인증 실패: $e" }
            SecurityContextHolder.clearContext()
        }
        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else null
    }
}