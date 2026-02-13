package com.monovoca.server.wordbook.domain.model.enumerated

enum class ColumnType(
    val level: Int,
    val isDefaultEnabled: Boolean,
    val defaultWidth: Int,
) {
    /* LEVEL 1 */
    SPELLING(level = 1, isDefaultEnabled = true, defaultWidth = 200),

    /* LEVEL 2 */
    DEFINITION(level = 2, isDefaultEnabled = true, defaultWidth = 200),
    ENG_DEFINITION(2, isDefaultEnabled = false, defaultWidth = 200),
    US_PHONETIC(2, isDefaultEnabled = false, defaultWidth = 100),
    UK_PHONETIC(2, isDefaultEnabled = false, defaultWidth = 100),
    PART_OF_SPEECH(2, isDefaultEnabled = true, defaultWidth = 100),
    SYNONYMS(2, isDefaultEnabled = false, defaultWidth = 150),
    ANTONYMS(2, isDefaultEnabled = false, defaultWidth = 150),

    /* LEVEL 3 */
    SENTENCE(3, isDefaultEnabled = true, defaultWidth = 300),
    TRANSLATION(3, isDefaultEnabled = false, defaultWidth = 200),
}