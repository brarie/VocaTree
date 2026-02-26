package com.vocatree.server.library.infrastructure.web.request

import com.vocatree.server.library.domain.model.enumerated.TrustedLevel

data class MakeSharedBundleRequest(
    val title: String,
    val description: String?,
)

data class UpdateSharedBundleMetadataRequest(
    val title: String,
    val description: String?,
)

data class UpdateBundleTrustedLevelRequest(
    val trustedLevel: TrustedLevel,
)
