package com.monovoca.server.common.infrastructure.exception

import com.monovoca.server.common.domain.exception.DomainException

data class ErrorResponse(
    val status: Int,
    val code: String,
    val message: String,
) {

    companion object {
        fun from(domainException: DomainException): ErrorResponse {
            return ErrorResponse(
                status = domainException.status.value(),
                message = domainException.message,
                code = domainException.code
            )
        }
    }
}