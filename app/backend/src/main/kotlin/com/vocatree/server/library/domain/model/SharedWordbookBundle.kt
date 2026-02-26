package com.vocatree.server.library.domain.model

import com.vocatree.server.common.util.generateSlug
import com.vocatree.server.library.domain.model.enumerated.SharedState
import com.vocatree.server.library.domain.model.enumerated.TrustedLevel
import com.vocatree.server.library.domain.model.exception.SharedWordbookBundleForbiddenException
import java.time.LocalDateTime

data class SharedWordbookBundle(
    val id: Long? = null,
    val creatorId: String,
    val title: String,
    val slug: String?,
    val description: String?,
    val sharedState: SharedState = SharedState.PUBLIC,
    val trustedLevel: TrustedLevel = TrustedLevel.BASIC,
    val createdAt: LocalDateTime,
    // todo: 나중에 사진도 추가
) {
    fun validateOwner(userId: String) {
        if (creatorId != userId) throw SharedWordbookBundleForbiddenException()
    }

    fun updateSlug(): SharedWordbookBundle {
        require(id != null)
        return this.copy(slug = generateSlug(title, id))
    }

    fun updateMetadata(title: String, description: String?): SharedWordbookBundle {
        require(id != null)
        return this.copy(
            title = title,
            description = description,
            slug = generateSlug(title, id),
        )
    }

    fun delete(): SharedWordbookBundle = this.copy(sharedState = SharedState.DELETED_BY_OWNER)

    fun ban(): SharedWordbookBundle = this.copy(sharedState = SharedState.BANNED)

    fun updateTrustedLevel(trustedLevel: TrustedLevel): SharedWordbookBundle = this.copy(trustedLevel = trustedLevel)
}
