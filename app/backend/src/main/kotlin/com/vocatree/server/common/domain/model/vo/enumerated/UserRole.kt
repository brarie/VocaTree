package com.vocatree.server.common.domain.model.vo.enumerated

enum class UserRole(
    val authority: String
) {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");
}