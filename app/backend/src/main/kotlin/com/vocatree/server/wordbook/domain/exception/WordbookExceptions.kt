package com.vocatree.server.wordbook.domain.exception

import com.vocatree.server.common.domain.exception.BadRequestException
import com.vocatree.server.common.domain.exception.ForbiddenException
import com.vocatree.server.common.domain.exception.NotFoundException

class WordbookForbiddenException() : ForbiddenException(
    code = "WORDBOOK_FORBIDDEN_EXCEPTION",
    message = "해당 단어장에 접근 권한이 없습니다."
)

class WordbookBundleForbiddenException() : ForbiddenException(
    code = "WORDBOOK_BUNDLE_FORBIDDEN_EXCEPTION",
    message = "해당 단어장 모음에 접근 권한이 없습니다."
)

class WordbookNotFoundException() : NotFoundException(
    code = "WORDBOOK_NOT_FOUND_EXCEPTION",
    message = "해당 단어장을 찾을 수 없습니다."
)

class WordbookBundleNotFoundException() : NotFoundException(
    code = "WORDBOOK_BUNDLE_NOT_FOUND_EXCEPTION",
    message = "해당 단어장 모음을 찾을 수 없습니다."
)

class CannotDeleteWordbookBundleWithWordbooks() : BadRequestException(
    code = "CANNOT_DELETE_WORDBOOK_BUNDLE_WITH_WORDBOOKS",
    message = "단어장이 포함된 단어장 모음은 삭제할 수 없습니다.",
)