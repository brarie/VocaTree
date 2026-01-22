package com.monovoca.server.auth.service.dto

data class TokenResult(
    val accessToken: String,
    val refreshToken: String,
    val accessExpMillis: Long,
    val refreshExpMillis: Long,
)