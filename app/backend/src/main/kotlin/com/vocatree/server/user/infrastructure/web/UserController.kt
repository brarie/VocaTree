package com.vocatree.server.user.infrastructure.web

import com.vocatree.server.common.infrastructure.UserPrincipal
import com.vocatree.server.user.application.UserService
import com.vocatree.server.user.infrastructure.web.response.UserInfo
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @GetMapping("/me")
    fun getCurrentUser(@AuthenticationPrincipal userPrincipal: UserPrincipal): ResponseEntity<UserInfo> =
        ResponseEntity.ok(UserInfo.from(userService.findById(userPrincipal.id)))
}