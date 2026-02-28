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

class BannedSharedWordbookNotInaccessibleException() : ForbiddenException(
    code = "BANNED_SHARED_WORDBOOK_INACCESSIBLE",
    message = "차단된 공유 단어장입니다."
)

class DeletedSharedWordbookInaccessibleException() : ForbiddenException(
    code = "DELETED_SHARED_WORDBOOK_INACCESSIBLE",
    message = "삭제된 공유 단어장입니다."
)