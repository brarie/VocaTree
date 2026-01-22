package com.monovoca.server.common.infrastructure

import com.monovoca.server.common.service.port.DateHolder
import org.springframework.stereotype.Component

@Component
class SystemDateHolder: DateHolder {

    override fun now(): Long {
        return System.currentTimeMillis()
    }
}