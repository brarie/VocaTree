package com.monovoca.server.common.infrastructure.holder

import com.monovoca.server.common.domain.port.holder.DateHolder
import org.springframework.stereotype.Component

@Component
class SystemDateHolder: DateHolder {

    override fun now(): Long {
        return System.currentTimeMillis()
    }
}