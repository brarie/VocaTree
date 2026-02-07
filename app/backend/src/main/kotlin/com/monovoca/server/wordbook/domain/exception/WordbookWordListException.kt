package com.monovoca.server.wordbook.domain.exception

import com.monovoca.server.common.domain.exception.NotFoundException

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