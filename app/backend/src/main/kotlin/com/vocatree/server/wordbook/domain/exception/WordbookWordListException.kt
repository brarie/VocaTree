package com.vocatree.server.wordbook.domain.exception

import com.vocatree.server.common.domain.exception.BadRequestException
import com.vocatree.server.common.domain.exception.NotFoundException

class WordbookWordNotFoundException : NotFoundException(
    code = "WORDBOOK_WORD_NOT_FOUND",
    message = "단어장 단어를 찾을 수 없습니다."
)

class WordbookSenseNotFoundException : NotFoundException(
    code = "WORDBOOK_SENSE_NOT_FOUND",
    message = "단어장 단어 뜻을 찾을 수 없습니다."
)

class WordbookExampleNotFoundException : NotFoundException(
    code = "WORDBOOK_EXAMPLE_NOT_FOUND",
    message = "단어장 단어 예문을 찾을 수 없습니다."
)

class WordbookOneHundredWordsLimitExceededException : BadRequestException(
    code = "WORDBOOK_ONE_HUNDRED_WORDS_LIMIT_EXCEEDED",
    message = "단어장 단어는 최대 100개까지 추가할 수 있습니다."
)