package be.howest.tuurdelacroix.careconnect.models.local

import java.time.LocalDateTime

data class Event(
    val title: String,
    val date: LocalDateTime) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Event

        if (title != other.title) return false
        if (date != other.date) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + date.hashCode()
        return result
    }
}