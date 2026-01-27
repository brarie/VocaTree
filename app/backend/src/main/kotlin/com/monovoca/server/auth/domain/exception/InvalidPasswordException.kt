package com.monovoca.server.auth.domain.exception

import com.monovoca.server.common.domain.exception.UnauthorizedException

class InvalidPasswordException : UnauthorizedException(
    code = "INVALID_PASSWORD",
    message = "비밀번호가 일치하지 않습니다."
)