package com.monovoca.server.common.infrastructure.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.monovoca.server.common.infrastructure.exception.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

@Component
class CustomAccessDeniedHandler : AccessDeniedHandler {

    private val objectMapper = ObjectMapper().registerKotlinModule()

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        response.status = HttpServletResponse.SC_FORBIDDEN
        response.contentType = "application/json;charset=UTF-8"

        val errorResponse = ErrorResponse(
            status = 403,
            code = "FORBIDDEN",
            message = "접근 권한이 없습니다."
        )
        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }
}
