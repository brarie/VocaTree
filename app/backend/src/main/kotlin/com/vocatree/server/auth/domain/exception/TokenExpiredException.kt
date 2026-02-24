package com.vocatree.server.auth.domain.exception

import com.vocatree.server.common.domain.exception.UnauthorizedException

class TokenExpiredException : UnauthorizedException(
    code = "TOKEN_EXPIRED",
    message = "토큰이 만료되었습니다."
)