package com.vocatree.server.library.domain.model.exception

import com.vocatree.server.common.domain.exception.ForbiddenException
import com.vocatree.server.common.domain.exception.NotFoundException

class SharedWordbookNotFoundException() : NotFoundException(
    code = "SHARED_WORDBOOK_NOT_FOUND",
    message = "해당 공유 단어장이 존재하지 않습니다."
)

class SharedWordbookForbiddenException() : ForbiddenException(
    code = "SHARED_WORDBOOK_FORBIDDEN",
    message = "공유 단어장에 접근할 권한이 없습니다."
)

class SharedWordbookRevisionNotFoundException() : NotFoundException(
    code = "SHARED_WORDBOOK_REVISION_NOT_FOUND",
    message = "해당 버전의 공유 단어장이 존재하지 않습니다."
)

class SharedWordbookBundleNotFoundException() : NotFoundException(
    code = "SHARED_WORDBOOK_BUNDLE_NOT_FOUND",
    message = "해당 공유 단어장 번들이 존재하지 않습니다."
)

class SharedWordbookBundleForbiddenException() : ForbiddenException(
    code = "SHARED_WORDBOOK_BUNDLE_FORBIDDEN",
    message = "공유 단어장 번들에 접근할 권한이 없습니다."
)