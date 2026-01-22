package com.monovoca.server.user.domain.port

import com.monovoca.server.user.domain.model.vo.Email

interface CertificationEmailSender {

    fun sendCertification(email: Email, code: String)
}