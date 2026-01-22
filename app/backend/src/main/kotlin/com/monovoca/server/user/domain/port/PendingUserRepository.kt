package com.monovoca.server.user.domain.port

import com.monovoca.server.common.domain.model.vo.Username
import com.monovoca.server.user.domain.model.User
import com.monovoca.server.user.domain.model.vo.Email

/**
 * 인증을 아직 안한 회원가입 유저들을 보관하는 저장소
 * TTL: 5분간 지속된다. (구조상 시간을 함수에 주입해야 스펙 상 표기가 될 것 같은데 불필요할 거 같아서 보류)
 */
interface PendingUserRepository {

    fun save(tempKey: String, user: User, code: String)
    fun existsByUsername(username: Username): Boolean
    fun existsByEmail(email: Email): Boolean
    fun findByTempKey(tempKey: String): User?
    fun findByCode(code: String): User?
    fun updateCode(tempKey: String, newCode: String)
    fun deleteAllByCode(code: String)
}