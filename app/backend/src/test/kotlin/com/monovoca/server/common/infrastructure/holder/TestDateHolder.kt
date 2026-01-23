package com.monovoca.server.common.infrastructure.holder

import com.monovoca.server.common.domain.port.holder.DateHolder
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Component
@Primary
class TestDateHolder : DateHolder {
    private var counter = 0
    private val times = mutableListOf<Long>()

    fun setTimes(vararg millis: Long) {
        times.clear()
        times.addAll(millis.toList())
        counter = 0
    }

    fun reset() {
        counter = 0
        times.clear()
    }

    override fun now(): Long {
        return if (counter < times.size) {
            times[counter++]
        } else {
            System.currentTimeMillis()
        }
    }
}
