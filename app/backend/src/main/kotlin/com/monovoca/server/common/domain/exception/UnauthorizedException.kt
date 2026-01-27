package com.monovoca.server.common.domain.exception

import org.springframework.http.HttpStatus

abstract class UnauthorizedException(
    code: String,
    message: String,
) : DomainException(
    status = HttpStatus.UNAUTHORIZED,
    code = code,
    message = message
)
