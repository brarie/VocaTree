package com.vocatree.server.common.domain.port.holder

import java.time.LocalDateTime

/**
 * LocalDateTime을 다룰 때 사용하는 Holder
 */
interface LocalDateTimeHolder {

    fun now(): LocalDateTime
}