package com.vocatree.server.library.infrastructure.web.request

import com.vocatree.server.library.domain.model.enumerated.TrustedLevel

data class ShareMyWordbookRequest(
    val title: String,
    val description: String?,
    val myWordbookId: Long,
    val versionNote: String,
)

data class UpdateSharedWordbookMetadataRequest(
    val title: String,
    val description: String?,
)

data class MakeNewVersionRequest(
    val myWordbookId: Long,
    val versionNote: String,
)

data class UpdateTrustedLevelRequest(
    val trustedLevel: TrustedLevel,
)
