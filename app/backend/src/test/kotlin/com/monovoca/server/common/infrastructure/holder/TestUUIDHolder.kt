package com.monovoca.server.common.infrastructure.holder

import com.monovoca.server.common.domain.port.holder.UUIDHolder
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Component
@Primary
class TestUUIDHolder : UUIDHolder {
    private var counter = 0
    private val uuids = mutableListOf<String>()

    fun setUUIDs(vararg ids: String) {
        uuids.clear()
        uuids.addAll(ids)
        counter = 0
    }

    fun reset() {
        counter = 0
        uuids.clear()
    }

    override fun generateUUID(): String {
        return if (counter < uuids.size) {
            uuids[counter++]
        } else {
            "test-uuid-${counter++}"
        }
    }
}
