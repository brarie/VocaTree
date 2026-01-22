package com.monovoca.server.common.domain.model.vo

enum class UserRole(
    val authority: String
) {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");
}