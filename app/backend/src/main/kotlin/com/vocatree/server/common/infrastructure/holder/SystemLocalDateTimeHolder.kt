package com.vocatree.server.common.infrastructure.holder

import com.vocatree.server.common.domain.port.holder.LocalDateTimeHolder
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class SystemLocalDateTimeHolder : LocalDateTimeHolder {

    override fun now(): LocalDateTime = LocalDateTime.now()
}