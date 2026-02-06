package com.monovoca.server.wordbook.domain.model

import com.monovoca.server.wordbook.domain.exception.WordbookNotFoundException
import com.monovoca.server.wordbook.domain.model.enumerated.NodeType
import java.time.LocalDateTime

// AGGREGATE //
data class Wordbook(
    override val id: Long? = null,
    override val title: String,
    override val ownerId: String,
    override val isStarred: Boolean = false,
    override val createdAt: LocalDateTime = LocalDateTime.now(),
    val bundleId: Long? = null,
) : WordbookNode() {

    override val nodeType: NodeType = NodeType.SINGLE

    override fun updateTitle(newTitle: String): Wordbook =
        this.copy(title = newTitle)

    override fun toggleStarred(): Wordbook =
        this.copy(isStarred = !isStarred)

    override fun validateOwner(userId: String) {
        if (ownerId != userId)
            throw WordbookNotFoundException()
    }

    fun moveToBundle(newBundleId: Long?): Wordbook =
        this.copy(bundleId = newBundleId)
}