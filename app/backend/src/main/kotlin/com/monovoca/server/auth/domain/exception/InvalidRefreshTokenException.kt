package com.monovoca.server.auth.domain.exception

import com.monovoca.server.common.domain.exception.UnauthorizedException

class InvalidRefreshTokenException : UnauthorizedException(
    code = "INVALID_REFRESH_TOKEN",
    message = "유효하지 않은 리프레시 토큰입니다."
)