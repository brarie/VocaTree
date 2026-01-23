package com.monovoca.server.common.domain.port.holder

/**
 * Date class를 다룰 때 사용하는 interface
 * Long을 리턴하기 때문에 나중에 Date의 생성자에 now()를 넣어주면 Date를 얻을 수 있다.
 * 몇몇 외부 라이브러리를 쉽게 다루기 위해 도입. (ex. jjwt)
 */
interface DateHolder {

    /**
     * 현재 시간을 밀리초 단위로 반환
     */
    fun now(): Long
}