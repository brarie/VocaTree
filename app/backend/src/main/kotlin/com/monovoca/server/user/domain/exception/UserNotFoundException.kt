package com.monovoca.server.user.domain.exception

import com.monovoca.server.common.domain.exception.NotFoundException

class UserNotFoundException : NotFoundException(
    code = "USER_NOT_FOUND",
    message = "사용자를 찾을 수 없습니다."
)