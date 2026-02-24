package com.vocatree.server.auth.infrastructure.web.response

data class LoginResponse(
    val accessToken: String,
    val accessExpSec: Int,
)