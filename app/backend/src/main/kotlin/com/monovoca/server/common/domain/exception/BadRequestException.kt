package com.monovoca.server.common.domain.exception

import org.springframework.http.HttpStatus

abstract class BadRequestException(
    code: String,
    message: String,
) : DomainException(
    status = HttpStatus.BAD_REQUEST,
    code = code,
    message = message
)
