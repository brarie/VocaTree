package com.vocatree.server.auth.domain.port

import com.vocatree.server.auth.domain.exception.InvalidAccessTokenException
import com.vocatree.server.auth.domain.exception.TokenExpiredException
import com.vocatree.server.auth.domain.model.AuthToken

interface AccessTokenProvider {

    fun issue(authToken: AuthToken): String

    @Throws(InvalidAccessTokenException::class, TokenExpiredException::class)
    fun parse(accessToken: String): AuthToken
}