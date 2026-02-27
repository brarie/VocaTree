package com.vocatree.server.library.domain.model

import com.vocatree.server.common.util.generateSlug
import com.vocatree.server.library.domain.model.enumerated.SharedState
import com.vocatree.server.library.domain.model.enumerated.TrustedLevel
import com.vocatree.server.library.domain.model.exception.SharedWordbookForbiddenException
import java.time.LocalDateTime

data class SharedWordbook(
    val id: Long? = null,
    val creatorId: String,
    val title: String,
    val slug: String?,
    val description: String?,
    val sharedState: SharedState = SharedState.PUBLIC,
    val trustedLevel: TrustedLevel = TrustedLevel.BASIC,
    val createdAt: LocalDateTime,
) {

    fun validateOwner(userId: String) {
        if (creatorId != userId)
            throw SharedWordbookForbiddenException()
    }

    fun updateSlug(): SharedWordbook {
        require(id != null)
        return this.copy(slug = generateSlug(title, id))
    }

    fun updateMetadata(title: String, description: String?): SharedWordbook {
        require(id != null)
        return this.copy(
            title = title,
            description = description,
            slug = generateSlug(title, id)
        )
    }

    fun delete(): SharedWordbook = this.copy(sharedState = SharedState.DELETED_BY_OWNER)

    fun ban(): SharedWordbook = this.copy(sharedState = SharedState.BANNED)

    fun unban(): SharedWordbook = this.copy(sharedState = SharedState.PUBLIC)

    fun updateTrustedLevel(trustedLevel: TrustedLevel): SharedWordbook = this.copy(trustedLevel = trustedLevel)
}