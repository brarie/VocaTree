package com.vocatree.server.common.infrastructure.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.core.task.SyncTaskExecutor
import java.util.concurrent.Executor

@TestConfiguration
class AsyncToSyncConfig {

    @Bean
    fun taskExecutor(): Executor = SyncTaskExecutor()
}
