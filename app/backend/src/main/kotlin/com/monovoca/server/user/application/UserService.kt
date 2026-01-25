package com.monovoca.server.user.application

import com.monovoca.server.user.domain.exception.UserNotFoundException
import com.monovoca.server.user.domain.model.User
import com.monovoca.server.user.domain.port.UserQueryRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userQueryRepository: UserQueryRepository
) {

    fun findById(id: String): User = userQueryRepository.findById(id) ?: throw UserNotFoundException()
}