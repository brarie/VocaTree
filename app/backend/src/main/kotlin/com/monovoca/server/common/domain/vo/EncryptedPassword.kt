package com.monovoca.server.common.domain.vo

import org.springframework.security.crypto.password.PasswordEncoder

object RawPasswordPolicy {
    const val regex = "^[A-Za-z\\d!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]{8,20}$"
    val pattern = regex.toRegex()
    const val message = "비밀번호는 영문, 숫자, 특수문자를 조합하여 8~20자로 설정해주세요."
}

@JvmInline
value class EncryptedPassword private constructor(val value: String) {

    companion object {
        fun encrypt(rawPassword: String, encoder: PasswordEncoder): EncryptedPassword {
            require(RawPasswordPolicy.pattern.matches(rawPassword)) {
                RawPasswordPolicy.message
            }

            return EncryptedPassword(encoder.encode(rawPassword))
        }

        fun fromHash(hashedPassword: String): EncryptedPassword {
            require(hashedPassword.isNotBlank()) { "해시된 비밀번호는 비어있을 수 없습니다." }
            return EncryptedPassword(hashedPassword)
        }
    }

    fun matches(rawPassword: String, encoder: PasswordEncoder): Boolean =
        encoder.matches(rawPassword, value)
}