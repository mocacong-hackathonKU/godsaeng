package com.mocacong.godsaeng.data

data class CalendarEntity(
    val date: String,
    val status: String,
    var isSelected: Boolean = false
) {
    companion object {
        val DAYS = listOf<String>("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
    }

    val year: String
        get() {
            val parts = date.split("-")
            return parts.getOrNull(0) ?: ""
        }

    val month: String
        get() {
            val parts = date.split("-")
            return parts.getOrNull(1) ?: ""
        }

    val day: String
        get() {
            val parts = date.split("-")
            val dayString = parts.getOrNull(2) ?: return ""
            return dayString.toInt().toString()
        }


    override fun toString(): String {
        return date
    }

    override fun equals(other: Any?): Boolean {
        if (other is CalendarEntity) {
            if (other.date == this.date) return true
        }
        return false
    }

    override fun hashCode(): Int {
        return date.hashCode()
    }
}
