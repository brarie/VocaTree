package com.monovoca.server.common.domain.port

interface DateHolder {

    /**
     * 현재 시간을 밀리초 단위로 반환
     */
    fun now(): Long
}