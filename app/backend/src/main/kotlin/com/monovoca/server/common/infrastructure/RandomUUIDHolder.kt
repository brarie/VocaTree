package com.monovoca.server.common.infrastructure

import com.monovoca.server.common.domain.port.UUIDHolder
import org.springframework.stereotype.Component
import java.util.*

@Component
class RandomUUIDHolder : UUIDHolder {

    override fun generateUUID(): String = UUID.randomUUID().toString()
}