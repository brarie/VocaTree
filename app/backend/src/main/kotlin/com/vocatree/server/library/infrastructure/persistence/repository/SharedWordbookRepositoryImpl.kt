package com.vocatree.server.library.infrastructure.persistence.repository

import com.vocatree.server.common.domain.model.query.SimpleUserView
import com.vocatree.server.library.domain.model.SharedWordbook
import com.vocatree.server.library.domain.model.enumerated.SharedState
import com.vocatree.server.library.domain.model.enumerated.TrustedLevel
import com.vocatree.server.library.domain.model.query.SharedWordbookDetailView
import com.vocatree.server.library.domain.model.query.SharedWordbookListView
import com.vocatree.server.library.domain.port.SharedWordbookRepository
import com.vocatree.server.library.domain.port.options.SharedWordbookSortType
import com.vocatree.server.library.infrastructure.persistence.entity.SharedWordbookEntity
import com.vocatree.server.library.infrastructure.persistence.entity.SharedWordbookRevisionTable
import com.vocatree.server.library.infrastructure.persistence.entity.SharedWordbookTable
import com.vocatree.server.user.infrastructure.persistence.entity.UserTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.UUID
import java.util.UUID.*

@Repository
class SharedWordbookRepositoryImpl : SharedWordbookRepository {

    override fun findById(sharedWordbookId: Long): SharedWordbook? {
        return SharedWordbookEntity.findById(sharedWordbookId)?.toDomain()
    }

    override fun save(sharedWordbook: SharedWordbook): SharedWordbook {
        val entity = if (sharedWordbook.id == null) {
            SharedWordbookEntity.new { applyDomain(sharedWordbook) }
        } else {
            SharedWordbookEntity.findById(sharedWordbook.id)!!.also { it.applyDomain(sharedWordbook) }
        }
        return entity.toDomain()
    }

    override fun paginatePublicViews(
        titlePrefix: String?,
        trustedLevels: List<TrustedLevel>,
        sortType: SharedWordbookSortType,
        pageable: Pageable,
    ): Page<SharedWordbookListView> {
        val conditionBuilder: SqlExpressionBuilder.() -> Op<Boolean> = {
            val conditions = mutableListOf<Op<Boolean>>()
            conditions.add(SharedWordbookTable.sharedState eq SharedState.PUBLIC)
            if (!titlePrefix.isNullOrBlank()) {
                conditions.add(SharedWordbookTable.title like "$titlePrefix%")
            }
            if (trustedLevels.isNotEmpty()) {
                conditions.add(SharedWordbookTable.trustedLevel inList trustedLevels)
            }
            conditions.compoundAnd()
        }

        val total = SharedWordbookTable.selectAll().where(conditionBuilder).count()

        val sortOrder = when (sortType) {
            SharedWordbookSortType.CREATED_AT_DESC -> SharedWordbookTable.createdAt to SortOrder.DESC
            // TODO: SUBSCRIPTION_COUNT_DESC requires subscription tracking table
            SharedWordbookSortType.SUBSCRIPTION_COUNT_DESC -> SharedWordbookTable.createdAt to SortOrder.DESC
        }

        val maxVersionAlias = SharedWordbookRevisionTable.version.max().alias("latest_version")

        val trustedLevelOrder = Case()
            .When(SharedWordbookTable.trustedLevel eq TrustedLevel.OFFICIAL, intLiteral(1))
            .When(SharedWordbookTable.trustedLevel eq TrustedLevel.VERIFIED, intLiteral(2))
            .Else(intLiteral(3))

        val content = SharedWordbookTable
            .innerJoin(UserTable, { SharedWordbookTable.creatorId }, { UserTable.id })
            .innerJoin(SharedWordbookRevisionTable, { SharedWordbookTable.id }, { SharedWordbookRevisionTable.sharedWordbookId })
            .select(
                SharedWordbookTable.id,
                SharedWordbookTable.title,
                SharedWordbookTable.slug,
                SharedWordbookTable.sharedState,
                SharedWordbookTable.trustedLevel,
                SharedWordbookTable.createdAt,
                UserTable.id,
                UserTable.nickname,
                maxVersionAlias,
            )
            .where(conditionBuilder)
            .groupBy(SharedWordbookTable.id, UserTable.id)
            .orderBy(trustedLevelOrder to SortOrder.ASC, sortOrder, SharedWordbookTable.id to SortOrder.DESC)
            .limit(pageable.pageSize)
            .offset(pageable.offset)
            .map { row ->
                SharedWordbookListView(
                    id = row[SharedWordbookTable.id].value,
                    title = row[SharedWordbookTable.title],
                    slug = row[SharedWordbookTable.slug]!!,
                    sharedState = row[SharedWordbookTable.sharedState],
                    trustedLevel = row[SharedWordbookTable.trustedLevel],
                    creator = SimpleUserView(
                        id = row[UserTable.id].value.toString(),
                        nickname = row[UserTable.nickname],
                    ),
                    latestVersion = row[maxVersionAlias]!!,
                    createdAt = row[SharedWordbookTable.createdAt],
                )
            }

        return PageImpl(content, pageable, total)
    }

    override fun paginateByCreatorId(creatorId: String, pageable: Pageable): Page<SharedWordbookListView> {
        val condition: SqlExpressionBuilder.() -> Op<Boolean> = {
            SharedWordbookTable.creatorId eq fromString(creatorId)
        }

        val total = SharedWordbookTable.selectAll().where(condition).count()

        val maxVersionAlias = SharedWordbookRevisionTable.version.max().alias("latest_version")

        val content = SharedWordbookTable
            .innerJoin(UserTable, { SharedWordbookTable.creatorId }, { UserTable.id })
            .innerJoin(SharedWordbookRevisionTable, { SharedWordbookTable.id }, { SharedWordbookRevisionTable.sharedWordbookId })
            .select(
                SharedWordbookTable.id,
                SharedWordbookTable.title,
                SharedWordbookTable.slug,
                SharedWordbookTable.sharedState,
                SharedWordbookTable.trustedLevel,
                SharedWordbookTable.createdAt,
                UserTable.id,
                UserTable.nickname,
                maxVersionAlias,
            )
            .where(condition)
            .groupBy(SharedWordbookTable.id, UserTable.id)
            .orderBy(SharedWordbookTable.createdAt to SortOrder.DESC, SharedWordbookTable.id to SortOrder.DESC)
            .limit(pageable.pageSize)
            .offset(pageable.offset)
            .map { row ->
                SharedWordbookListView(
                    id = row[SharedWordbookTable.id].value,
                    title = row[SharedWordbookTable.title],
                    slug = row[SharedWordbookTable.slug]!!,
                    sharedState = row[SharedWordbookTable.sharedState],
                    trustedLevel = row[SharedWordbookTable.trustedLevel],
                    creator = SimpleUserView(
                        id = row[UserTable.id].value.toString(),
                        nickname = row[UserTable.nickname],
                    ),
                    latestVersion = row[maxVersionAlias]!!,
                    createdAt = row[SharedWordbookTable.createdAt],
                )
            }

        return PageImpl(content, pageable, total)
    }

    override fun findDetailView(sharedWordbookId: Long): SharedWordbookDetailView? {
        return (SharedWordbookTable
            .innerJoin(UserTable, { SharedWordbookTable.creatorId }, { UserTable.id })
            .innerJoin(SharedWordbookRevisionTable, { SharedWordbookTable.id }, { SharedWordbookRevisionTable.sharedWordbookId }))
            .selectAll()
            .where { SharedWordbookTable.id eq sharedWordbookId }
            .orderBy(SharedWordbookRevisionTable.version to SortOrder.DESC)
            .limit(1)
            .firstOrNull()
            ?.let { row ->
                SharedWordbookDetailView(
                    id = row[SharedWordbookTable.id].value,
                    title = row[SharedWordbookTable.title],
                    slug = row[SharedWordbookTable.slug]!!,
                    description = row[SharedWordbookTable.description],
                    sharedState = row[SharedWordbookTable.sharedState],
                    trustedLevel = row[SharedWordbookTable.trustedLevel],
                    creator = SimpleUserView(
                        id = row[UserTable.id].value.toString(),
                        nickname = row[UserTable.nickname],
                    ),
                    createdAt = row[SharedWordbookTable.createdAt],
                    latestVersion = row[SharedWordbookRevisionTable.version],
                    latestRevisionId = row[SharedWordbookRevisionTable.id].value,
                )
            }
    }
}
