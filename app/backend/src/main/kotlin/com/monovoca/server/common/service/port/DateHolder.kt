package com.monovoca.server.common.service.port

interface DateHolder {

    /**
     * 현재 시간을 밀리초 단위로 반환
     */
    fun now(): Long
}