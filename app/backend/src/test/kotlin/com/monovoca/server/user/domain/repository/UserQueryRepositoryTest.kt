package com.monovoca.server.user.domain.repository

import com.monovoca.server.user.domain.model.vo.Email
import com.monovoca.server.user.domain.model.vo.Username
import com.monovoca.server.user.fixture.UserFixture
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ApplyExtension(SpringExtension::class)
@Transactional
class UserQueryRepositoryTest(
    private val userQueryRepository: UserQueryRepository,
    private val userCommandRepository: UserCommandRepository
) : DescribeSpec({
    describe("existsByUsername") {
        it("username이 존재하면 true를 반환한다.") {
            val user = UserFixture.create(
                username = "testuser1",
                email = "monovoca@naver.com"
            )
            userCommandRepository.save(user)
            val exists = userQueryRepository.existsByUsername(Username("testuser1"))
            exists shouldBe true
        }
        it("username이 존재하지 않으면 false를 반환한다.") {
            val exists = userQueryRepository.existsByUsername(Username("nouser1234"))
            exists shouldBe false
        }
    }
    describe("existsByEmail") {
        it("email이 존재하면 true를 반환한다.") {
            val user = UserFixture.create(
                username = "testuser1",
                email = "monovoca@naver.com"
            )
            userCommandRepository.save(user)
            val exists = userQueryRepository.existsByEmail(Email("monovoca@naver.com"))
            exists shouldBe true
        }
        it("email이 존재하지 않으면 false를 반환한다.") {
            val exists = userQueryRepository.existsByEmail(Email("nonexistent@naver.com"))
            exists shouldBe false
        }
    }
})
