package com.vocatree.server.common.infrastructure.config

import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.StringRedisTemplate

@SpringBootTest
@ApplyExtension(SpringExtension::class)
@Import(RedisTestConfig::class)
class RedisConnectionTest(
    private val redisTemplate: StringRedisTemplate
) : DescribeSpec({

    describe("Redis 연결 (TestContainers)") {
        it("TestContainers Redis에 값을 저장하고 조회할 수 있다.") {
            val key = "test:connection"
            val value = "hello-redis"

            redisTemplate.opsForValue().set(key, value)
            val result = redisTemplate.opsForValue().get(key)

            result shouldBe value

            redisTemplate.delete(key)
        }
    }
})
