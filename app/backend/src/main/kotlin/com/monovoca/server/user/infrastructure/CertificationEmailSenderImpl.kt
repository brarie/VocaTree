package com.monovoca.server.user.infrastructure

import com.monovoca.server.user.domain.model.vo.Email
import com.monovoca.server.user.domain.port.CertificationEmailSender
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class CertificationEmailSenderImpl(
    private val mailSender: JavaMailSender,
    @param:Value("\${app.certificate-redirect-url}") private val url: String,
) : CertificationEmailSender {

    @Async
    override fun sendCertification(email: Email, code: String) {
        val redirectURL = "$url?code=$code"
        val message = mailSender.createMimeMessage()
        MimeMessageHelper(message, true, "UTF-8").apply {
            setTo(email.value)
            setSubject("[Monovoca] 이메일 인증을 완료해주세요.")
            setText(
                """
                <html>
                    <body>
                        <h1>이메일 인증</h1>
                        <p>아래 링크를 클릭하여 이메일 인증을 완료해주세요.</p>
                        <a href="$redirectURL">$redirectURL</a>
                    </body>
                </html>
                """.trimIndent(), true
            )
        }
        mailSender.send(message)
    }
}