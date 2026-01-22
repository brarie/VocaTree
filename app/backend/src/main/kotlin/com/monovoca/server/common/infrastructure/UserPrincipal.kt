package com.monovoca.server.common.infrastructure

import com.monovoca.server.common.domain.model.vo.UserRole
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/**
 * @AuthenticationPrincipal을 통해 infrastructure/web 에서 사용할 수 있다.
 * (Controller에서 user의 id를 가져올 수 있는 객체)
 */
class UserPrincipal(
    val id: String,
    val role: UserRole
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf(GrantedAuthority { role.authority })

    override fun getPassword(): String? = null

    override fun getUsername(): String = id
}