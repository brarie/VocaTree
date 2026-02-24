package com.vocatree.server.user.application

import com.vocatree.server.common.domain.model.vo.EncryptedPassword
import com.vocatree.server.common.domain.model.vo.Username
import com.vocatree.server.common.domain.port.holder.UUIDHolder
import com.vocatree.server.user.domain.exception.*
import com.vocatree.server.user.domain.model.User
import com.vocatree.server.user.domain.model.vo.Email
import com.vocatree.server.user.domain.model.vo.Nickname
import com.vocatree.server.user.domain.port.CertificationEmailSender
import com.vocatree.server.user.domain.port.PendingUserRepository
import com.vocatree.server.user.domain.port.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserRegisterService(
    private val userRepository: UserRepository,
    private val pendingUserRepository: PendingUserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val certificationEmailSender: CertificationEmailSender,
    private val uuidHolder: UUIDHolder
) {

    @Transactional
    fun signUp(usernameStr: String, emailStr: String, nicknameStr: String, rawPassword: String): String {
        val email = Email(emailStr).also {
            if (userRepository.existsByEmail(it)) throw DuplicatedEmailException()
        }
        val username = Username(usernameStr).also {
            if (userRepository.existsByUsername(it)) throw DuplicatedUsernameException()
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

        userRepository.save(user)
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