package com.monovoca.server.common.infrastructure.config

import com.monovoca.server.auth.infrastructure.security.JWTAuthFilter
import com.monovoca.server.common.infrastructure.security.CustomAccessDeniedHandler
import com.monovoca.server.common.infrastructure.security.CustomAuthenticationEntryPoint
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jwtAuthFilter: JWTAuthFilter,
    private val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint,
    private val customAccessDeniedHandler: CustomAccessDeniedHandler
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .csrf { it.disable() }
            .cors { cors -> cors.configurationSource(corsConfigurationSource()) }
            .exceptionHandling {
                    it.authenticationEntryPoint(customAuthenticationEntryPoint)
                    it.accessDeniedHandler(customAccessDeniedHandler)
            }
            .authorizeHttpRequests {
                it.requestMatchers(PathRequest.toH2Console()).permitAll()
                it.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                it.requestMatchers("/api/auth/login", "/api/auth/refresh").permitAll()
                it.requestMatchers("/api/users/sign-up", "/api/users/verify", "/api/users/resend-verification").permitAll()
                it.requestMatchers("/api/support-categories/**", "/api/support-posts/**").permitAll()
                it.requestMatchers(HttpMethod.GET, "/api/dictionary/search", "/api/dictionary/words/slug/{slug}", "/api/dictionary/search/autocomplete", "/api/dictionary/words", "/api/dictionary/count", "/api/dictionary/words/spelling/{spelling}").permitAll()
                it.anyRequest().authenticated()
            }
            .headers { it.frameOptions { it.sameOrigin() } }

        http.sessionManagement {
            it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        }

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("http://localhost:3000")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}