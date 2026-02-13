package com.monovoca.server.common.infrastructure.exception

import com.monovoca.server.common.domain.exception.DomainException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.validation.BindException
import org.springframework.validation.method.MethodValidationException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    private val klogger = KotlinLogging.logger {}

    /**
     * BindException 등을 처리하는 override method.
     * 나중에 errorMessage를 ErrorResponse 에 예쁘게 삽입해야 할 때가 오면 그때 수정
     */
    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<in Any>? {
        klogger.warn { "MethodArgumentNotValidException: ${ex.message}" }
        val errorMessage = ex.bindingResult.allErrors.joinToString("; ") { it.defaultMessage ?: "Invalid value" }
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    status = HttpStatus.BAD_REQUEST.value(),
                    code = "BAD_REQUEST",
                    message = errorMessage
                )
            )
    }

    @ExceptionHandler(DomainException::class)
    fun handleDomainException(ex: DomainException): ResponseEntity<ErrorResponse> {
        klogger.warn { "DomainException: ${ex.code} - ${ex.message}" }
        return ResponseEntity
            .status(ex.status)
            .body(ErrorResponse.from(ex))
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(ex: AccessDeniedException): ResponseEntity<ErrorResponse> {
        klogger.warn { "Access denied: ${ex.message}" }
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(
                ErrorResponse(
                    status = HttpStatus.FORBIDDEN.value(),
                    code = "FORBIDDEN",
                    message = "접근 권한이 없습니다."
                )
            )
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<ErrorResponse> {
        klogger.error(ex) { "Unexpected exception: ${ex.message}" }
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ErrorResponse(
                    status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    code = "INTERNAL_SERVER_ERROR",
                    message = "예기치 않은 오류가 발생했습니다."
                )
            )
    }
}