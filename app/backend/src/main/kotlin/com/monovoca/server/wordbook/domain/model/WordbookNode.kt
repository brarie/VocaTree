package com.monovoca.server.wordbook.domain.model

import com.monovoca.server.wordbook.domain.model.enumerated.NodeType
import java.time.LocalDateTime

sealed class WordbookNode {
    abstract val id: Long?
    abstract val title: String
    abstract val ownerId: String
    abstract val isStarred: Boolean
    abstract val nodeType: NodeType
    abstract val createdAt: LocalDateTime

    abstract fun updateTitle(newTitle: String): WordbookNode
    abstract fun toggleStarred(): WordbookNode

    /**
     * @Throws com.monovoca.server.common.domain.exception.ForbiddenException
     */
    abstract fun validateOwner(userId: String)
}