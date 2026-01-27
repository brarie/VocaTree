package com.monovoca.server.common.domain.exception

import org.springframework.http.HttpStatus

abstract class NotFoundException(
    code: String,
    message: String,
): DomainException(
    status = HttpStatus.NOT_FOUND,
    code = code,
    message = message
)