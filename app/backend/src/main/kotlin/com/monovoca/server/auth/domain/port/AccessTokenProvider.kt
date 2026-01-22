package com.monovoca.server.auth.domain.port

import com.monovoca.server.auth.domain.exception.InvalidAccessTokenException
import com.monovoca.server.auth.domain.exception.TokenExpiredException
import com.monovoca.server.auth.domain.model.AuthToken

interface AccessTokenProvider {

    fun issue(authToken: AuthToken): String

    @Throws(InvalidAccessTokenException::class, TokenExpiredException::class)
    fun parse(accessToken: String): AuthToken
}