package com.monovoca.server.user.domain.model.vo

object RawPasswordPolicy {
    const val regex = "^[A-Za-z\\d!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]{8,20}$"
    val pattern = regex.toRegex()
    const val message = "비밀번호는 영문, 숫자, 특수문자를 조합하여 8~20자로 설정해주세요."
}

@JvmInline
value class EncryptedPassword private constructor(val value: String) {

    companion object {
        fun encrypt(rawPassword: String, encryptor: (String) -> String): EncryptedPassword {
            require(RawPasswordPolicy.pattern.matches(rawPassword)) {
                RawPasswordPolicy.message
            }

            return EncryptedPassword(encryptor(rawPassword))
        }

        fun fromHash(hashedPassword: String): EncryptedPassword {
            require(hashedPassword.isNotBlank()) { "해시된 비밀번호는 비어있을 수 없습니다." }
            return EncryptedPassword(hashedPassword)
        }
    }

    fun match(rawPassword: String, encryptor: (String) -> String): Boolean =
        value == encryptor(rawPassword)
}