package com.vocatree.server.library.domain.model

import java.time.LocalDateTime

data class SharedWordbookRevision(
    val id: Long? = null,
    val version: Long,
    val versionNote: String? = null,
    val revisedAt: LocalDateTime,
)