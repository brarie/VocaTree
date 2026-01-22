package com.monovoca.server.common.infrastructure.config

import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetup
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class GreenMailTestConfig {

    @Bean
    fun greenMail(): GreenMail {
        val serverSetup = ServerSetup(3025, "localhost", ServerSetup.PROTOCOL_SMTP)
        return GreenMail(serverSetup).apply { start() }
    }
}
