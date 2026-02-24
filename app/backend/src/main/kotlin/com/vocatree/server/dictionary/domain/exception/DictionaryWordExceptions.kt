package com.vocatree.server.dictionary.domain.exception

import com.vocatree.server.common.domain.exception.ConflictException
import com.vocatree.server.common.domain.exception.NotFoundException

class PublicWordAlreadyExistsException : ConflictException(
    code = "PUBLIC_WORD_ALREADY_EXISTS",
    message = "해당 단어가 이미 존재합니다."
)

class PublicWordNotFoundException : NotFoundException(
    code = "PUBLIC_WORD_NOT_FOUND",
    message = "해당 단어를 찾을 수 없습니다."
)

class PublicSenseNotFoundException : NotFoundException(
    code = "PUBLIC_SENSE_NOT_FOUND",
    message = "해당 의미를 찾을 수 없습니다."
)

class PublicExampleNotFoundException : NotFoundException(
    code = "PUBLIC_EXAMPLE_NOT_FOUND",
    message = "해당 예문을 찾을 수 없습니다."
)
