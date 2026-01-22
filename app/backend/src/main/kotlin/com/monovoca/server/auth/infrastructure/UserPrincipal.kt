package com.monovoca.server.auth.infrastructure

import com.monovoca.server.common.domain.vo.UserRole
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserPrincipal(
    val id: String,
    val role: UserRole
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf(GrantedAuthority { role.authority })

    override fun getPassword(): String? = null

    override fun getUsername(): String = id
}