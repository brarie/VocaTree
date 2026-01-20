package com.monovoca.server.common.domain.vo

enum class UserRole(
    val authority: String
) {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");
}