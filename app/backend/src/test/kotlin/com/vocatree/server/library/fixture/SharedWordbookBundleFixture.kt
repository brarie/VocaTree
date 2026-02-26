package com.vocatree.server.library.fixture

import com.vocatree.server.library.domain.model.SharedWordbookBundle
import com.vocatree.server.library.domain.model.enumerated.SharedState
import com.vocatree.server.library.domain.model.enumerated.TrustedLevel
import java.time.LocalDateTime

object SharedWordbookBundleFixture {

    fun create(
        id: Long? = null,
        creatorId: String = "00000000-0000-0000-0000-000000000001",
        title: String = "테스트 번들",
        slug: String? = null,
        description: String? = null,
        sharedState: SharedState = SharedState.PUBLIC,
        trustedLevel: TrustedLevel = TrustedLevel.BASIC,
        createdAt: LocalDateTime = LocalDateTime.of(2025, 1, 1, 12, 0),
    ): SharedWordbookBundle = SharedWordbookBundle(
        id = id,
        creatorId = creatorId,
        title = title,
        slug = slug,
        description = description,
        sharedState = sharedState,
        trustedLevel = trustedLevel,
        createdAt = createdAt,
    )
}
