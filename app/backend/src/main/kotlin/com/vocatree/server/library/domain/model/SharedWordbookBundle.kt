package com.vocatree.server.library.domain.model

import com.vocatree.server.library.domain.model.enumerated.SharedState
import com.vocatree.server.library.domain.model.enumerated.TrustedLevel

data class SharedWordbookBundle(
    val id: Long?,
    val creatorId: String,
    val title: String,
    val slug: String?,
    val description: String?,
    val sharedState: SharedState,
    val trustedLevel: TrustedLevel = TrustedLevel.BASIC,
    // todo: 나중에 사진도 추가
)
