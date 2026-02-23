package com.monovoca.server.user.application

import com.monovoca.server.user.domain.exception.UserNotFoundException
import com.monovoca.server.user.domain.model.User
import com.monovoca.server.user.domain.port.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository
) {

    @Transactional(readOnly = true)
    fun findById(id: String): User = userRepository.findById(id) ?: throw UserNotFoundException()
}