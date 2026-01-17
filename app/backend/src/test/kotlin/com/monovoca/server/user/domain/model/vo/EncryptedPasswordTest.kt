package com.monovoca.server.user.domain.model.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class EncryptedPasswordTest : DescribeSpec({
    describe("EncryptedPassword 생성") {
        it("DB에서 가져온 암호화된 비밀번호를 통해 객체가 생성된다.") {
            val password = EncryptedPassword.fromHash("hashed_password_1234")
            password.value shouldBe "hashed_password_1234"
        }
        it("DB에서 가져온 암호화된 비밀번호가 비어있으면 IllegalArgumentException 예외가 발생한다.") {
            shouldThrow<IllegalArgumentException> {
                EncryptedPassword.fromHash("   ")
            }
        }
        it("원본 비밀번호를 통해 암호화된 비밀번호를 만들 수 있다.") {
            val rawPassword = "abcd@1234"
            val encryptedPassword = EncryptedPassword.encrypt(rawPassword, { raw -> "encrypted_$raw"} )
            encryptedPassword.value shouldBe "encrypted_abcd@1234"
        }
        it("원본 비밀번호와 암호화된 비밀번호가 일치하는지 확인할 수 있다.") {
            val rawPassword = "abcd@1234"
            val encryptedPassword = EncryptedPassword.encrypt(rawPassword, { raw -> "encrypted_$raw"} )
            val isMatch_1 = encryptedPassword.match("abcd@1234", { raw -> "encrypted_$raw"})
            val isMatch_2 = encryptedPassword.match("abcde@1234", { raw -> "encrypted_$raw"})
            isMatch_1 shouldBe true
            isMatch_2 shouldBe false
        }
        it("비밀번호의 길이가 8자 미만 또는 20자 초과이면 IllegalArgumentException 예외가 발생한다.") {
            val rawPasswords = listOf("aB3@567", "aB3@56789012345678901")
            rawPasswords.forEach {
                shouldThrow<IllegalArgumentException> {
                    EncryptedPassword.encrypt(it, { raw -> "encrypted_$raw" })
                }
            }
        }
        it("비밀번호에 영문, 숫자, 특수문자를 제외한 다른 문자가 들어가면 IllegalArgumentException 예외가 발생한다.") {
            val rawPasswords = listOf("abcd 한글 1234!", "abcd\t1234!", "abcd\n1234!", "abcd😊1234!", "          ")
            rawPasswords.forEach {
                shouldThrow<IllegalArgumentException> {
                    EncryptedPassword.encrypt(it, { raw -> "encrypted_$raw" })
                }
            }
        }
    }
})