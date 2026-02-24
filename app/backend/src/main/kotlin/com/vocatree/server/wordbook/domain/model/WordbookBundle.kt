package com.vocatree.server.wordbook.domain.model

import com.vocatree.server.wordbook.domain.exception.WordbookBundleForbiddenException
import com.vocatree.server.wordbook.domain.model.enumerated.NodeType
import java.time.LocalDateTime

// AGGREGATE //
data class WordbookBundle(
    override val id: Long? = null,
    override val title: String,
    override val ownerId: String,
    override val isStarred: Boolean = false,
    override val createdAt: LocalDateTime = LocalDateTime.now(),
) : WordbookNode() {

    override val nodeType: NodeType = NodeType.BUNDLE

    override fun updateTitle(newTitle: String): WordbookBundle =
        this.copy(title = newTitle)

    override fun toggleStarred(): WordbookBundle =
        this.copy(isStarred = !isStarred)

    override fun validateOwner(userId: String) {
        if (ownerId != userId)
            throw WordbookBundleForbiddenException()
    }
}