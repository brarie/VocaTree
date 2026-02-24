package com.vocatree.server.wordbook.domain.exception

import com.vocatree.server.common.domain.exception.BadRequestException

class CannotDeactivateSpellingColumnException : BadRequestException(
    code = "CANNOT_DEACTIVATE_SPELLING_COLUMN",
    message = "단어 칼럼은 비활성화할 수 없습니다."
)

class MissingParentDependencyColumnException : BadRequestException(
    code = "MISSING_PARENT_DEPENDENCY_COLUMN",
    message = "해당 칼럼을 활성화하기 위해서는 상위 칼럼을 먼저 활성화해야 합니다."
)

class ParentColumnDeactivationRestrictedException : BadRequestException(
    code = "PARENT_COLUMN_DEACTIVATION_RESTRICTED",
    message = "하위 칼럼이 활성화된 상태에서 모든 상위 칼럼을 비활성화할 수는 없습니다."
)