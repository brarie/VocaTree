package com.vocatree.server.common.domain.exception

import org.springframework.http.HttpStatus

abstract class ForbiddenException(
    code: String,
    message: String,
) : DomainException(
    status = HttpStatus.FORBIDDEN,
    code = code,
    message = message
)
