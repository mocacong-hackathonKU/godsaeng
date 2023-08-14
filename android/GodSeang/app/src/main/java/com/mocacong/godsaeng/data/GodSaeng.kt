package com.mocacong.godsaeng.data

sealed class GodSaeng(
    val title: String,
    val status: String
) {
    enum class STATUS {
        BEFORE,
        PROCEEDING,
        DONE;
    }
    class DailyInfo(id: Long, title: String, status: String) : GodSaeng(title, status) {
        var isEnabled: Boolean = status == STATUS.PROCEEDING.name
        val proofString: String = if (status == STATUS.PROCEEDING.name) "인증하기" else "인증완료"
    }

    class Detail(
        title: String,
        description: String,
        weeks: List<String>,
        openDate: String,
        closeDate: String,
        members: List<Member>,
        progress: Int,
        proofs: List<Proof>
    )
}