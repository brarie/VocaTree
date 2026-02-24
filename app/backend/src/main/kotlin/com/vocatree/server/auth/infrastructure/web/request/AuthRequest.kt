package com.vocatree.server.auth.infrastructure.web.request

data class LoginRequest(
    val username: String,
    val rawPassword: String
)