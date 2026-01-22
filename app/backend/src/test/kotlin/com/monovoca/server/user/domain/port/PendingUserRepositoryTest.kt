package com.monovoca.server.user.domain.port

import com.monovoca.server.common.domain.model.vo.Username
import com.monovoca.server.common.infrastructure.config.RedisTestConfig
import com.monovoca.server.user.domain.model.vo.Email
import com.monovoca.server.user.fixture.UserFixture
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
class PendingUserRepositoryTest(
    private val pendingUserRepository: PendingUserRepository,
    private val redisTemplate: StringRedisTemplate
) : DescribeSpec({

    beforeEach {
        redisTemplate.delete(redisTemplate.keys("*"))
    }

    val user = UserFixture.create()
    val tempKey = "temp-key"
    val code = "cert-code"

    describe("save") {
        it("유저를 저장할 수 있다.") {
            pendingUserRepository.save(tempKey, user, code)
        }
    }

    describe("findByTempKey") {
        it("저장된 유저를 tempKey로 조회할 수 있다.") {
            pendingUserRepository.save(tempKey, user, code)

            val found = pendingUserRepository.findByTempKey(tempKey)

            found?.username shouldBe user.username
            found?.email shouldBe user.email
        }

        it("존재하지 않는 tempKey로 조회하면 null을 반환한다.") {
            val found = pendingUserRepository.findByTempKey("nonexistent")
            found shouldBe null
        }
    }

    describe("findByCode") {
        it("저장된 유저를 code로 조회할 수 있다.") {
            pendingUserRepository.save(tempKey, user, code)

            val found = pendingUserRepository.findByCode(code)

            found?.username shouldBe user.username
            found?.email shouldBe user.email
        }

        it("존재하지 않는 code로 조회하면 null을 반환한다.") {
            val found = pendingUserRepository.findByCode("nonexistent")
            found shouldBe null
        }
    }

    describe("existsByUsername") {
        it("저장된 username이 존재하면 true를 반환한다.") {
            pendingUserRepository.save(tempKey, user, code)

            val exists = pendingUserRepository.existsByUsername(user.username)
            exists shouldBe true
        }

        it("저장되지 않은 username은 false를 반환한다.") {
            val exists = pendingUserRepository.existsByUsername(Username("nonexist"))
            exists shouldBe false
        }
    }

    describe("existsByEmail") {
        it("저장된 email이 존재하면 true를 반환한다.") {
            pendingUserRepository.save(tempKey, user, code)

            val exists = pendingUserRepository.existsByEmail(user.email)
            exists shouldBe true
        }

        it("저장되지 않은 email은 false를 반환한다.") {
            val exists = pendingUserRepository.existsByEmail(Email("nonexistent@example.com"))
            exists shouldBe false
        }
    }

    describe("updateCode") {
        it("code를 업데이트하면 새로운 code로 조회할 수 있다.") {
            val newCode = "new-code"
            pendingUserRepository.save(tempKey, user, code)

            pendingUserRepository.updateCode(tempKey, newCode)

            val found = pendingUserRepository.findByCode(newCode)
            found?.username shouldBe user.username
        }

        it("code를 업데이트하면 기존 code로는 조회할 수 없다.") {
            val newCode = "new-code"
            pendingUserRepository.save(tempKey, user, code)

            pendingUserRepository.updateCode(tempKey, newCode)

            val found = pendingUserRepository.findByCode(code)
            found shouldBe null
        }
    }

    describe("deleteAllByCode") {
        it("code로 삭제하면 tempKey로 조회할 수 없다.") {
            pendingUserRepository.save(tempKey, user, code)

            pendingUserRepository.deleteAllByCode(code)

            val found = pendingUserRepository.findByTempKey(tempKey)
            found shouldBe null
        }

        it("code로 삭제하면 username 존재 여부가 false가 된다.") {
            pendingUserRepository.save(tempKey, user, code)

            pendingUserRepository.deleteAllByCode(code)

            val exists = pendingUserRepository.existsByUsername(user.username)
            exists shouldBe false
        }

        it("code로 삭제하면 email 존재 여부가 false가 된다.") {
            pendingUserRepository.save(tempKey, user, code)

            pendingUserRepository.deleteAllByCode(code)

            val exists = pendingUserRepository.existsByEmail(user.email)
            exists shouldBe false
        }
    }
})
