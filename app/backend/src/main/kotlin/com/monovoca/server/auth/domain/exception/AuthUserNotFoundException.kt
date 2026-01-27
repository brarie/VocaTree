package com.monovoca.server.auth.domain.exception

import com.monovoca.server.common.domain.exception.NotFoundException

class AuthUserNotFoundException : NotFoundException(
    code = "AUTH_USER_NOT_FOUND",
    message = "사용자를 찾을 수 없습니다."
)