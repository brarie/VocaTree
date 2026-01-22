package com.monovoca.server.auth.service.port

interface RefreshTokenRepository {

    /**
     * 리프레시 토큰 저장
     * refreshToken과 userId는 1:1 매핑이여야 한다. 마지막 것을 기준으로 한다.
     */
    fun save(refreshToken: String, userId: String, refreshExpMillis: Long)
    fun findUserIdByRefreshToken(refreshToken: String): String?
    fun deleteByUserId(userId: String)
}