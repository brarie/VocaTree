package com.vocatree.server.user.application

import com.vocatree.server.user.domain.exception.UserNotFoundException
import com.vocatree.server.user.domain.model.User
import com.vocatree.server.user.domain.port.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository
) {

    @Transactional(readOnly = true)
    fun findById(id: String): User = userRepository.findById(id) ?: throw UserNotFoundException()
}