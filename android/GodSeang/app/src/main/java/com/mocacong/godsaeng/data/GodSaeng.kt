package com.mocacong.godsaeng.data

open class GodSaeng(
    open val title: String,
    open val status: String
) {
    enum class STATUS {
        BEFORE,
        PROCEEDING,
        DONE;
    }

    protected val mapping = mapOf(
        "MON" to "월",
        "TUE" to "화",
        "WED" to "수",
        "THU" to "목",
        "FRI" to "금",
        "SAT" to "토",
        "SUN" to "일"
    )

    protected fun convertWeeksToKorean(weeks: List<String>): String {
        return weeks.joinToString(", ") { mapping[it] ?: it }
    }
}

data class DailyInfo(val id: Long, override val title: String, override val status: String) :
    GodSaeng(title = title, status = status) {
    var isEnabled: Boolean = status == STATUS.PROCEEDING.name
    val proofString: String = if (status == STATUS.PROCEEDING.name) "인증하기" else "인증완료"
}

data class Detail(
    val id: Long,
    override val title: String,
    val description: String,
    val weeks: List<String>,
    val openDate: String,
    val closeDate: String,
    val members: List<Member>,
    val progress: Int,
    override val status: String,
    val proofs: List<Proof>
) : GodSaeng(title, status) {
    val weeksStr: String = convertWeeksToKorean(weeks)
}

data class Preview(
    val id: Long,
    override val title: String,
    val description: String,
    val week: List<String>
) : GodSaeng(title = title, "") {
    val weeksStr: String = convertWeeksToKorean(week)
}
