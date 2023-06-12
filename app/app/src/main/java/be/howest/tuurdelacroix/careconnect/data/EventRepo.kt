package be.howest.tuurdelacroix.careconnect.data

import android.os.Build
import androidx.annotation.RequiresApi
import be.howest.tuurdelacroix.careconnect.models.local.Event
import java.time.LocalDate
import java.time.LocalDateTime

object EventRepo {
    @RequiresApi(Build.VERSION_CODES.O) //todo change to data from api
    val events = listOf(
        Event("TUUR KOMT LANGS", LocalDateTime.of(2023,4,17,19,30,0)),
        Event("TIJS KOMT LANGS", LocalDateTime.of(2023,4,17,20,0,0)) //todo change to date type
    )

    @RequiresApi(Build.VERSION_CODES.O)
    fun getEventsOfToday(): List<Event> {
        val today = LocalDate.now()
        return events.filter { it.date.toLocalDate().isEqual(today) }
    }
}