package com.vocatree.server.user.domain.port

import com.vocatree.server.user.domain.model.vo.Email

interface CertificationEmailSender {

    fun sendCertification(email: Email, code: String)
}