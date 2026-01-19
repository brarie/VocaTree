package com.monovoca.server.user.service.port

import com.monovoca.server.user.domain.model.vo.Email

interface CertificationEmailSender {

    fun sendCertification(email: Email, code: String)
}