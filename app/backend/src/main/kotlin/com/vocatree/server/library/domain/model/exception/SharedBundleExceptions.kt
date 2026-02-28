package com.vocatree.server.library.domain.model.exception

import com.vocatree.server.common.domain.exception.ForbiddenException
import com.vocatree.server.common.domain.exception.NotFoundException


class SharedWordbookBundleNotFoundException() : NotFoundException(
    code = "SHARED_WORDBOOK_BUNDLE_NOT_FOUND",
    message = "해당 공유 단어장 번들이 존재하지 않습니다."
)

class SharedWordbookBundleForbiddenException() : ForbiddenException(
    code = "SHARED_WORDBOOK_BUNDLE_FORBIDDEN",
    message = "공유 단어장 번들에 접근할 권한이 없습니다."
)

class BannedSharedWordbookBundleInaccessibleException() : ForbiddenException(
    code = "BANNED_SHARED_WORDBOOK_BUNDLE_INACCESSIBLE",
    message = "차단된 공유 번들입니다."
)

class DeletedSharedWordbookBundleInaccessibleException() : ForbiddenException(
    code = "DELETED_SHARED_WORDBOOK_BUNDLE_INACCESSIBLE",
    message = "삭제된 공유 번들입니다."
)