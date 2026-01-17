package com.monovoca.server.common.config

import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.ignoringRequestMatchers(PathRequest.toH2Console()) }
            .authorizeHttpRequests { it.requestMatchers(PathRequest.toH2Console()).permitAll() }
            .headers { it.frameOptions { it.sameOrigin() } }

        return http.build()
    }
}