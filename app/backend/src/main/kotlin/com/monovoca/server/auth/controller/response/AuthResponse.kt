package com.monovoca.server.auth.controller.response

data class LoginResponse(
    val accessToken: String,
    val accessExpSec: Int,
)