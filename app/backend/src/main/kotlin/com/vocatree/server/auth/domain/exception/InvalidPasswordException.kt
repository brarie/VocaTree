package com.vocatree.server.auth.domain.exception

import com.vocatree.server.common.domain.exception.UnauthorizedException

class InvalidPasswordException : UnauthorizedException(
    code = "INVALID_PASSWORD",
    message = "비밀번호가 일치하지 않습니다."
)