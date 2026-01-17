package com.monovoca.server.user.domain.model.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class UsernameTest : DescribeSpec({
    describe("Username 생성") {
        it("올바른 형식의 아이디면 객체가 생성된다.") {
            val validUsernames = listOf("user", "user1234", "abcd123456", "USER1234", "Test1234")
            val usernames = validUsernames.map { Username(it) }
            usernames.forEachIndexed { index, username ->
                username.value shouldBe validUsernames[index]
            }
        }
        it("아이디 길이가 4자 미만이면 IllegalArgumentException 예외가 발생한다.") {
            val invalidUsernames = listOf("a", "ab", "abc")
            invalidUsernames.forEach {
                shouldThrow<IllegalArgumentException> {
                    Username(it)
                }
            }
        }
        it("아이디 길이가 10자 초과면 IllegalArgumentException 예외가 발생한다.") {
            val invalidUsernames = listOf("abcdefghijk", "user12345678")
            invalidUsernames.forEach {
                shouldThrow<IllegalArgumentException> {
                    Username(it)
                }
            }
        }
        it("아이디에 영문, 숫자 이외의 문자가 포함되면 IllegalArgumentException 예외가 발생한다.") {
            val invalidUsernames = listOf("user!", "user@name", "user name", "유저1234", "user_123", "user-123")
            invalidUsernames.forEach {
                shouldThrow<IllegalArgumentException> {
                    Username(it)
                }
            }
        }
    }
})
