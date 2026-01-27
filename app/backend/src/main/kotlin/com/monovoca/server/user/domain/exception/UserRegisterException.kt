package com.monovoca.server.user.domain.exception

import com.monovoca.server.common.domain.exception.BadRequestException
import com.monovoca.server.common.domain.exception.ConflictException

class DuplicatedUsernameException : ConflictException(
    code = "DUPLICATED_USERNAME",
    message = "이미 존재하는 아이디입니다."
)

class DuplicatedEmailException : ConflictException(
    code = "DUPLICATED_EMAIL",
    message = "이미 존재하는 이메일입니다."
)

class AlreadyRegisteringUsernameException : ConflictException(
    code = "ALREADY_REGISTERING_USERNAME",
    message = "이미 가입 절차가 진행 중인 아이디입니다."
)

class AlreadyRegisteringEmailException : ConflictException(
    code = "ALREADY_REGISTERING_EMAIL",
    message = "이미 가입 절차가 진행 중인 이메일입니다."
)

class InvalidCertificationCodeException : BadRequestException(
    code = "INVALID_CERTIFICATION_CODE",
    message = "인증 코드가 유효하지 않습니다."
)

class InvalidTempKeyException : BadRequestException(
    code = "INVALID_TEMP_KEY",
    message = "임시 키가 유효하지 않습니다."
)