package com.monovoca.server.common.infrastructure.holder

import com.monovoca.server.common.domain.port.holder.LocalDateTimeHolder
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
@Primary
class TestLocalDateTimeHolder : LocalDateTimeHolder {
    private var counter = 0
    private val dateTimes = mutableListOf<LocalDateTime>()

    fun setDateTimes(vararg times: LocalDateTime) {
        dateTimes.clear()
        dateTimes.addAll(times.toList())
        counter = 0
    }

    fun reset() {
        counter = 0
        dateTimes.clear()
    }

    override fun now(): LocalDateTime {
        return if (counter < dateTimes.size) {
            dateTimes[counter++]
        } else {
            LocalDateTime.now()
        }
    }
}
