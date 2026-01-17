package com.monovoca.server.user.domain.model.vo

object EmailPolicy {
    const val regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    val pattern = regex.toRegex()
    const val message = "이메일 형식이 올바르지 않습니다."
}

@JvmInline
value class Email(val value: String) {
    init {
        require(EmailPolicy.pattern.matches(value)) { EmailPolicy.message }
    }
}