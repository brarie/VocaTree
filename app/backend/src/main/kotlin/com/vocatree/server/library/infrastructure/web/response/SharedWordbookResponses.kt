package com.vocatree.server.library.infrastructure.web.response

import com.vocatree.server.common.infrastructure.web.response.ExampleResponse
import com.vocatree.server.common.infrastructure.web.response.SenseResponse
import com.vocatree.server.library.domain.model.SharedExampleSnapshot
import com.vocatree.server.library.domain.model.SharedSenseSnapshot
import com.vocatree.server.library.domain.model.SharedWordSnapshot
import com.vocatree.server.library.domain.model.SharedWordbook
import com.vocatree.server.library.domain.model.SharedWordbookRevision
import com.vocatree.server.library.domain.model.enumerated.SharedState
import com.vocatree.server.library.domain.model.enumerated.TrustedLevel
import java.time.LocalDateTime

data class SharedWordbookResponse(
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
        fun from(wordbook: SharedWordbook): SharedWordbookResponse =
            SharedWordbookResponse(
                id = wordbook.id!!,
                title = wordbook.title,
                slug = wordbook.slug,
                description = wordbook.description,
                sharedState = wordbook.sharedState,
                trustedLevel = wordbook.trustedLevel,
                creatorId = wordbook.creatorId,
                createdAt = wordbook.createdAt,
            )
    }
}

data class SharedWordbookRevisionResponse(
    val id: Long,
    val version: Long,
    val versionNote: String?,
    val revisedAt: LocalDateTime,
) {
    companion object {
        fun from(revision: SharedWordbookRevision): SharedWordbookRevisionResponse =
            SharedWordbookRevisionResponse(
                id = revision.id!!,
                version = revision.version,
                versionNote = revision.versionNote,
                revisedAt = revision.revisedAt,
            )
    }
}

data class SharedWordSnapshotResponse(
    val id: Long,
    val spelling: String?,
    val displayOrder: Int,
    val senses: List<SenseSnapshotResponse>,
) {
    companion object {
        fun from(snapshot: SharedWordSnapshot): SharedWordSnapshotResponse =
            SharedWordSnapshotResponse(
                id = snapshot.id!!,
                spelling = snapshot.spelling?.value,
                displayOrder = snapshot.displayOrder,
                senses = snapshot.senses.map { SenseSnapshotResponse.from(it) },
            )
    }

    data class SenseSnapshotResponse(
        val sense: SenseResponse,
        val displayOrder: Int,
        val examples: List<ExampleSnapshotResponse>,
    ) {
        companion object {
            fun from(senseSnapshot: SharedSenseSnapshot): SenseSnapshotResponse =
                SenseSnapshotResponse(
                    sense = SenseResponse.from(senseSnapshot.sense),
                    displayOrder = senseSnapshot.displayOrder,
                    examples = senseSnapshot.examples.map { ExampleSnapshotResponse.from(it) },
                )
        }
    }

    data class ExampleSnapshotResponse(
        val example: ExampleResponse,
        val displayOrder: Int,
    ) {
        companion object {
            fun from(exampleSnapshot: SharedExampleSnapshot): ExampleSnapshotResponse =
                ExampleSnapshotResponse(
                    example = ExampleResponse.from(exampleSnapshot.example),
                    displayOrder = exampleSnapshot.displayOrder,
                )
        }
    }
}
