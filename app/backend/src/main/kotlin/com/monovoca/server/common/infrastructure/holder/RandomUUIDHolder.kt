package com.monovoca.server.common.infrastructure.holder

import com.monovoca.server.common.domain.port.holder.UUIDHolder
import org.springframework.stereotype.Component
import java.util.*

@Component
class RandomUUIDHolder : UUIDHolder {

    override fun generateUUID(): String = UUID.randomUUID().toString()
}