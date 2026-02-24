package com.vocatree.server.user.infrastructure.persistence.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.vocatree.server.common.domain.model.vo.Username
import com.vocatree.server.user.domain.model.User
import com.vocatree.server.user.domain.model.vo.Email
import com.vocatree.server.user.domain.port.PendingUserRepository
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration
import java.util.concurrent.TimeUnit

/**
 * Redis를 이용한 PendingUserRepository 구현체
 * reg:user:$tempKey       = JSON
 * reg:code:$tempKey       = code
 * reg:username:$username  = tempKey
 * reg:email:$email        = tempKey
 * reg:code-to-key:$code   = tempKey
 */
@Repository
class PendingUserRepositoryImpl(
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper
) : PendingUserRepository {

    override fun save(tempKey: String, user: User, code: String) {
        val userJson = objectMapper.writeValueAsString(user)
        redisTemplate.opsForValue().set("reg:user:$tempKey", userJson, Duration.ofMinutes(5))
        redisTemplate.opsForValue().set("reg:code:$tempKey", code, Duration.ofMinutes(5))
        redisTemplate.opsForValue().set("reg:username:${user.username.value}", tempKey, Duration.ofMinutes(5))
        redisTemplate.opsForValue().set("reg:email:${user.email.value}", tempKey, Duration.ofMinutes(5))
        redisTemplate.opsForValue().set("reg:code-to-key:$code", tempKey, Duration.ofMinutes(5))
    }

    override fun existsByUsername(username: Username): Boolean =
        redisTemplate.opsForValue().get("reg:username:${username.value}") != null

    override fun existsByEmail(email: Email): Boolean =
        redisTemplate.opsForValue().get("reg:email:${email.value}") != null

    override fun findByTempKey(tempKey: String): User? =
        redisTemplate.opsForValue().get("reg:user:$tempKey")?.let {
            objectMapper.readValue(it, User::class.java)
        }

    override fun findByCode(code: String): User? =
        redisTemplate.opsForValue().get("reg:code-to-key:$code")?.let {
            findByTempKey(it)
        }

    override fun updateCode(tempKey: String, newCode: String) {
        val ttl = redisTemplate.getExpire("reg:user:$tempKey")
        redisTemplate.opsForValue().get("reg:code:$tempKey")?.let {
            redisTemplate.delete("reg:code-to-key:$it")
        }
        redisTemplate.opsForValue().set("reg:code:$tempKey", newCode, ttl, TimeUnit.SECONDS)
        redisTemplate.opsForValue().set("reg:code-to-key:$newCode", tempKey, ttl, TimeUnit.SECONDS)
    }

    override fun deleteAllByCode(code: String) {
        val tempKey = redisTemplate.opsForValue().get("reg:code-to-key:$code") ?: return
        val user = findByTempKey(tempKey) ?: return
        redisTemplate.delete("reg:user:$tempKey")
        redisTemplate.delete("reg:code:$tempKey")
        redisTemplate.delete("reg:username:${user.username.value}")
        redisTemplate.delete("reg:email:${user.email.value}")
        redisTemplate.delete("reg:code-to-key:$code")
    }
}