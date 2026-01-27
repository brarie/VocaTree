package com.monovoca.server.common.domain.exception

import org.springframework.http.HttpStatus

abstract class ConflictException(
    code: String,
    message: String,
) : DomainException(
    status = HttpStatus.CONFLICT,
    code = code,
    message = message
)
