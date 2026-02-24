package com.vocatree.server.common.infrastructure.holder

import com.vocatree.server.common.domain.port.holder.DateHolder
import org.springframework.stereotype.Component

@Component
class SystemDateHolder: DateHolder {

    override fun now(): Long {
        return System.currentTimeMillis()
    }
}