package com.monovoca.server.user.service.port

import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.GreenMailUtil
import com.monovoca.server.common.config.AsyncToSyncConfig
import com.monovoca.server.common.config.GreenMailTestConfig
import com.monovoca.server.user.domain.model.vo.Email
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

/**
 * CertificationEmailSender의 이메일 발송 기능 테스트
 * 함수가 Async일 경우 테스트에서 메일 내부 내용 확인이 불가능하므로 AsyncToSyncConfig를 적용
 */
@SpringBootTest
@ApplyExtension(SpringExtension::class)
@Import(GreenMailTestConfig::class, AsyncToSyncConfig::class)
class CertificationEmailSenderTest(
    private val certificationEmailSender: CertificationEmailSender,
    private val greenMail: GreenMail
) : DescribeSpec({

    beforeEach {
        greenMail.reset()
    }

    describe("sendCertification") {
        it("인증 이메일을 올바르게 발송한다.") {
            val email = Email("test@example.com")
            val code = "cert-code-1"

            certificationEmailSender.sendCertification(email, code)

            greenMail.receivedMessages.size shouldBe 1

            val message = greenMail.receivedMessages[0]
            message.allRecipients[0].toString() shouldBe "test@example.com"
            GreenMailUtil.getBody(message) shouldContain code
        }
    }
})
