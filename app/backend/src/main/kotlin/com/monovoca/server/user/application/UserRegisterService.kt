package com.monovoca.server.user.application

import com.monovoca.server.common.domain.port.UUIDHolder
import com.monovoca.server.common.domain.model.vo.EncryptedPassword
import com.monovoca.server.common.domain.model.vo.Username
import com.monovoca.server.user.domain.exception.*
import com.monovoca.server.user.domain.model.User
import com.monovoca.server.user.domain.model.vo.Email
import com.monovoca.server.user.domain.model.vo.Nickname
import com.monovoca.server.user.domain.port.CertificationEmailSender
import com.monovoca.server.user.domain.port.PendingUserRepository
import com.monovoca.server.user.domain.port.UserCommandRepository
import com.monovoca.server.user.domain.port.UserQueryRepository
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
    fun signUp(usernameStr: String, emailStr: String, nicknameStr: String, rawPassword: String): String {
        val email = Email(emailStr).also {
            if (userQueryRepository.existsByEmail(it)) throw DuplicatedEmailException()
        }
        val username = Username(usernameStr).also {
            if (userQueryRepository.existsByUsername(it)) throw DuplicatedUsernameException()
        }
        val nickname = Nickname(nicknameStr)
        val password = EncryptedPassword.encrypt(rawPassword, passwordEncoder)
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

        return tempKey
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