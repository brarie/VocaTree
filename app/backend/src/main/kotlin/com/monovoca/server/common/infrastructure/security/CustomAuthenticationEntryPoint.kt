package com.monovoca.server.common.infrastructure.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.monovoca.server.common.infrastructure.exception.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationEntryPoint : AuthenticationEntryPoint {

    private val objectMapper = ObjectMapper().registerKotlinModule()

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = "application/json;charset=UTF-8"

        val errorResponse = ErrorResponse(
            status = 401,
            code = "UNAUTHORIZED",
            message = "인증이 필요합니다."
        )
        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }
}
