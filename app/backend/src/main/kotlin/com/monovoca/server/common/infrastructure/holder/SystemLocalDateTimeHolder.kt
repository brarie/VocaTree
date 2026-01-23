package com.monovoca.server.common.infrastructure.holder

import com.monovoca.server.common.domain.port.holder.LocalDateTimeHolder
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class SystemLocalDateTimeHolder : LocalDateTimeHolder {

    override fun now(): LocalDateTime = LocalDateTime.now()
}