package com.monovoca.server.user.domain.exception

class DuplicatedUsernameException: RuntimeException()
class DuplicatedEmailException: RuntimeException()
class AlreadyRegisteringUsernameException: RuntimeException()
class AlreadyRegisteringEmailException: RuntimeException()
class InvalidCertificationCodeException: RuntimeException()
class InvalidTempKeyException: RuntimeException()