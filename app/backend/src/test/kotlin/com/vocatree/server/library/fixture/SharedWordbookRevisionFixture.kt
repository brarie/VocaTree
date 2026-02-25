package com.vocatree.server.library.fixture

import com.vocatree.server.library.domain.model.SharedWordbookRevision
import java.time.LocalDateTime

object SharedWordbookRevisionFixture {

    fun create(
        id: Long? = null,
        version: Long = 1L,
        versionNote: String? = "첫 번째 버전",
        revisedAt: LocalDateTime = LocalDateTime.of(2025, 1, 1, 12, 0),
    ): SharedWordbookRevision = SharedWordbookRevision(
        id = id,
        version = version,
        versionNote = versionNote,
        revisedAt = revisedAt,
    )
}
