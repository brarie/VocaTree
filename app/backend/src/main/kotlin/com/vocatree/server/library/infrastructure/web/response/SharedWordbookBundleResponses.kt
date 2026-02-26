package com.vocatree.server.library.infrastructure.web.response

import com.vocatree.server.library.domain.model.SharedWordbookBundle
import com.vocatree.server.library.domain.model.enumerated.SharedState
import com.vocatree.server.library.domain.model.enumerated.TrustedLevel
import java.time.LocalDateTime

data class SharedWordbookBundleResponse(
    val id: Long,
    val title: String,
    val slug: String?,
    val description: String?,
    val sharedState: SharedState,
    val trustedLevel: TrustedLevel,
    val creatorId: String,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(bundle: SharedWordbookBundle): SharedWordbookBundleResponse =
            SharedWordbookBundleResponse(
                id = bundle.id!!,
                title = bundle.title,
                slug = bundle.slug,
                description = bundle.description,
                sharedState = bundle.sharedState,
                trustedLevel = bundle.trustedLevel,
                creatorId = bundle.creatorId,
                createdAt = bundle.createdAt,
            )
    }
}
