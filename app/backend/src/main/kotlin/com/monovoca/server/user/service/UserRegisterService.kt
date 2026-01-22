package com.monovoca.server.user.service

import com.monovoca.server.common.service.port.UUIDHolder
import com.monovoca.server.user.controller.request.SignUpRequest
import com.monovoca.server.user.controller.response.SignUpResponse
import com.monovoca.server.user.domain.exception.*
import com.monovoca.server.user.domain.model.User
import com.monovoca.server.user.domain.model.vo.Email
import com.monovoca.server.common.domain.vo.EncryptedPassword
import com.monovoca.server.user.domain.model.vo.Nickname
import com.monovoca.server.common.domain.vo.Username
import com.monovoca.server.user.domain.port.UserCommandRepository
import com.monovoca.server.user.domain.port.UserQueryRepository
import com.monovoca.server.user.service.port.CertificationEmailSender
import com.monovoca.server.user.service.port.PendingUserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserRegisterService(
    private val userCommandRepository: UserCommandRepository,
    private val userQueryRepository: UserQueryRepository,
    private val pendingUserRepository: PendingUserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val certificationEmailSender: CertificationEmailSender,
    private val uuidHolder: UUIDHolder
) {

    @Transactional
    fun signUp(signUpRequest: SignUpRequest): SignUpResponse {
        val email = Email(signUpRequest.email).also {
            if (userQueryRepository.existsByEmail(it)) throw DuplicatedEmailException()
        }
        val username = Username(signUpRequest.username).also {
            if (userQueryRepository.existsByUsername(it)) throw DuplicatedUsernameException()
        }
        val nickname = Nickname(signUpRequest.nickname)
        val password = EncryptedPassword.encrypt(signUpRequest.rawPassword, passwordEncoder)
        val user = User.create(
            username = username,
            email = email,
            nickname = nickname,
            password = password
        )

        if (pendingUserRepository.existsByEmail(email))
            throw AlreadyRegisteringEmailException()

        if (pendingUserRepository.existsByUsername(username))
            throw AlreadyRegisteringUsernameException()

        val tempKey = uuidHolder.generateUUID()
        val code = uuidHolder.generateUUID()
        pendingUserRepository.save(tempKey, user, code)

        certificationEmailSender.sendCertification(email, code)

        return SignUpResponse(tempKey)
    }

    @Transactional
    fun certificate(code: String) {
        val user = pendingUserRepository.findByCode(code)
            ?: throw InvalidCertificationCodeException()

        userCommandRepository.save(user)
        pendingUserRepository.deleteAllByCode(code)
    }

    @Transactional
    fun resendCertification(tempKey: String) {
        val user = pendingUserRepository.findByTempKey(tempKey)
            ?: throw InvalidTempKeyException()

        val newCode = uuidHolder.generateUUID()
        pendingUserRepository.updateCode(tempKey, newCode)

        certificationEmailSender.sendCertification(user.email, newCode)
    }
}