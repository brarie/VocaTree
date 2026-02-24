package com.vocatree.server.user.domain.exception

import com.vocatree.server.common.domain.exception.NotFoundException

class UserNotFoundException : NotFoundException(
    code = "USER_NOT_FOUND",
    message = "사용자를 찾을 수 없습니다."
)