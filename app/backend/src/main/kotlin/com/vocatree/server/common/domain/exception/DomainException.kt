package com.vocatree.server.common.domain.exception

import org.springframework.http.HttpStatus

sealed class DomainException(
    val status: HttpStatus,
    val code: String,
    override val message: String
) : RuntimeException(message)