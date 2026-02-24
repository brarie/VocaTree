package com.vocatree.server.user.domain.model

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.vocatree.server.user.fixture.UserFixture
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class UserSerializationTest : DescribeSpec({

    val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

    describe("User 직렬화/역직렬화") {
        it("User 객체를 JSON으로 직렬화하고 다시 역직렬화할 수 있다") {
            val user = UserFixture.create(
                username = "testuser",
                email = "test@example.com",
                nickname = "테스트",
                password = "hashedpassword123"
            )

            val json = objectMapper.writeValueAsString(user)
            val deserialized = objectMapper.readValue<User>(json)

            deserialized.username.value shouldBe user.username.value
            deserialized.email.value shouldBe user.email.value
            deserialized.nickname.value shouldBe user.nickname.value
            deserialized.password.value shouldBe user.password.value
        }
    }
})
