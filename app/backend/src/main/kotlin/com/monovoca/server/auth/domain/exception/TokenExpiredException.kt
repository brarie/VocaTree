package com.monovoca.server.auth.domain.exception

import com.monovoca.server.common.domain.exception.UnauthorizedException

class TokenExpiredException : UnauthorizedException(
    code = "TOKEN_EXPIRED",
    message = "토큰이 만료되었습니다."
)