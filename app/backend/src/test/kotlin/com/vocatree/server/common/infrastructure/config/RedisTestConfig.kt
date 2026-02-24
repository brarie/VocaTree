package com.vocatree.server.common.infrastructure.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration
class RedisTestConfig {

    companion object {
        private val redisContainer: GenericContainer<*> = GenericContainer(DockerImageName.parse("redis:latest"))
            .withExposedPorts(6379)
            .apply { start() }
    }

    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory {
        val config = RedisStandaloneConfiguration(
            redisContainer.host,
            redisContainer.getMappedPort(6379)
        )
        return LettuceConnectionFactory(config)
    }
}
