package com.vocatree.server.auth.application.dto

data class TokenResult(
    val accessToken: String,
    val refreshToken: String,
    val accessExpMillis: Long,
    val refreshExpMillis: Long,
)