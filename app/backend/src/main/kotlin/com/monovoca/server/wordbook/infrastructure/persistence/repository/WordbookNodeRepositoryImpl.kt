package com.monovoca.server.wordbook.infrastructure.persistence.repository

import com.monovoca.server.wordbook.domain.model.enumerated.NodeType
import com.monovoca.server.wordbook.domain.model.query.NodeResponseWithCountView
import com.monovoca.server.wordbook.domain.port.WordbookNodeRepository
import com.monovoca.server.wordbook.domain.port.options.WordbookNodeSortType
import com.monovoca.server.wordbook.infrastructure.persistence.entity.WordbookNodeTable
import com.monovoca.server.wordbook.infrastructure.persistence.entity.WordbookWordTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class WordbookNodeRepositoryImpl : WordbookNodeRepository {

    override fun paginateWithoutParent(
        ownerId: String,
        pageable: Pageable,
        wordbookNodeSortType: WordbookNodeSortType,
        starOnly: Boolean
    ): Page<NodeResponseWithCountView> = transaction {
        val ownerUUID = UUID.fromString(ownerId)
        val childNodeTableAlias = WordbookNodeTable.alias("child_node")
        val wordbookCountAlias = childNodeTableAlias[WordbookNodeTable.id].countDistinct().alias("wordbook_count")
        val wordCountAlias = WordbookWordTable.id.countDistinct().alias("word_count")
        val query = WordbookNodeTable
            .leftJoin(
                childNodeTableAlias,
                { WordbookNodeTable.id },
                { childNodeTableAlias[WordbookNodeTable.parentId] })
            .leftJoin(WordbookWordTable, { WordbookNodeTable.id }, { WordbookWordTable.wordbookId })
            .select(WordbookNodeTable.columns + wordbookCountAlias + wordCountAlias)
            .where {
                val conditions = mutableListOf<Op<Boolean>>()
                conditions.add(WordbookNodeTable.ownerId eq ownerUUID)
                conditions.add(WordbookNodeTable.parentId.isNull())
                if (starOnly) {
                    conditions.add(WordbookNodeTable.isStarred eq true)
                }
                conditions.compoundAnd()
            }
            .groupBy(WordbookNodeTable.id)

        val total = query.count()
        val content = query
            .limit(pageable.pageSize)
            .offset(pageable.offset)
            .orderBy(
                Case()
                    .When(WordbookNodeTable.nodeType eq NodeType.BUNDLE, intLiteral(0))
                    .Else(intLiteral(1)) to SortOrder.ASC,
                when (wordbookNodeSortType) {
                    WordbookNodeSortType.TITLE_ASC -> WordbookNodeTable.title to SortOrder.ASC
                    WordbookNodeSortType.TITLE_DESC -> WordbookNodeTable.title to SortOrder.DESC
                    WordbookNodeSortType.CREATED_AT_ASC -> WordbookNodeTable.createdAt to SortOrder.ASC
                    WordbookNodeSortType.CREATED_AT_DESC -> WordbookNodeTable.createdAt to SortOrder.DESC
                },
                WordbookNodeTable.id to SortOrder.DESC
            )
            .map {
                val nodeType = it[WordbookNodeTable.nodeType]
                NodeResponseWithCountView(
                    id = it[WordbookNodeTable.id].value,
                    title = it[WordbookNodeTable.title],
                    isStarred = it[WordbookNodeTable.isStarred],
                    nodeType = it[WordbookNodeTable.nodeType],
                    createdAt = it[WordbookNodeTable.createdAt],
                    count = when (nodeType) {
                        NodeType.BUNDLE -> it[wordbookCountAlias].toInt()
                        NodeType.SINGLE -> it[wordCountAlias].toInt()
                    }
                )
            }

        PageImpl(content, pageable, total)
    }

    override fun paginateInBundle(
        ownerId: String,
        bundleId: Long,
        pageable: Pageable
    ): Page<NodeResponseWithCountView> = transaction {
        val ownerUUID = UUID.fromString(ownerId)
        val wordCountAlias = WordbookWordTable.id.count().alias("word_count")
        val query = (WordbookNodeTable leftJoin WordbookWordTable)
            .select(WordbookNodeTable.columns + wordCountAlias)
            .where { (WordbookNodeTable.ownerId eq ownerUUID) and (WordbookNodeTable.parentId eq bundleId) }
            .groupBy(WordbookNodeTable.id)

        val total = query.count()

        val content = query
            .limit(pageable.pageSize)
            .offset(pageable.offset)
            .orderBy(
                WordbookNodeTable.title to SortOrder.ASC,
                WordbookNodeTable.id to SortOrder.DESC
            )
            .map {
                NodeResponseWithCountView(
                    id = it[WordbookNodeTable.id].value,
                    title = it[WordbookNodeTable.title],
                    isStarred = it[WordbookNodeTable.isStarred],
                    nodeType = it[WordbookNodeTable.nodeType],
                    createdAt = it[WordbookNodeTable.createdAt],
                    count = it[wordCountAlias].toInt()
                )
            }

        PageImpl(content, pageable, total)
    }
}