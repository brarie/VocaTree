package com.vocatree.server.auth.domain.exception

import com.vocatree.server.common.domain.exception.UnauthorizedException

class InvalidAccessTokenException : UnauthorizedException(
    code = "INVALID_ACCESS_TOKEN",
    message = "유효하지 않은 액세스 토큰입니다."
)