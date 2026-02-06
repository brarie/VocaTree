### Explain

설계가 좀 복잡하여 따로 정리

**WordbookBundle** (from WordbookNode)

**Wordbook** (from WordbookNode)

**WordbookWordList** -> WordbookWord -> WordbookSense -> WordbookExample

WordbookNode는 WordbookBundle과 Wordbook의 공통 속성을 모아둔 sealed class

WordbookWordList는 WordbookWord를 List 형태로 모아 관리를 편하게 하기 위한 클래스 (실제 DB에 저장되지 않음)<br>
이는 정합성 등을 위해 만들어둔 클래스임.

해당 도메인에서는 Aggregate root 가 총 3개이며, WordbookBundle, Wordbook, WordbookWordList 임.