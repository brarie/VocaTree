package com.monovoca.server.auth.controller.request

data class LoginRequest(
    val username: String,
    val rawPassword: String
)