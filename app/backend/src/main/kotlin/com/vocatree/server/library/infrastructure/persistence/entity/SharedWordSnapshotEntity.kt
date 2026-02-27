package com.vocatree.server.library.infrastructure.persistence.entity

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.vocatree.server.library.domain.model.SharedWordSnapshot
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.json.json
import java.security.MessageDigest

private val mapper = ObjectMapper()
    .registerModule(KotlinModule.Builder().build())
    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
    .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL)

object SharedWordSnapshotTable : LongIdTable("shared_word_snapshot") {
    val snapshot = json<SharedWordSnapshot>(
        "snapshot",
        { mapper.writeValueAsString(it) },
        { mapper.readValue(it) }
    )
    val checksum = varchar("checksum", 64).uniqueIndex()
}

class SharedWordSnapshotEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<SharedWordSnapshotEntity>(SharedWordSnapshotTable) {
        fun computeChecksum(snapshot: SharedWordSnapshot): String {
            val json = mapper.writeValueAsString(snapshot.copy(id = null))
            return MessageDigest.getInstance("SHA-256")
                .digest(json.toByteArray())
                .joinToString("") { "%02x".format(it) }
        }
    }

    var snapshot by SharedWordSnapshotTable.snapshot
    var checksum by SharedWordSnapshotTable.checksum

    fun toDomain(): SharedWordSnapshot = this.snapshot.copy(id = this.id.value)

    fun applyDomain(snapshot: SharedWordSnapshot) {
        this.snapshot = snapshot.copy(id = null)
        this.checksum = computeChecksum(snapshot)
    }
}
