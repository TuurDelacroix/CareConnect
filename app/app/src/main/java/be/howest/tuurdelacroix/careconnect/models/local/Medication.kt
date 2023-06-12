package be.howest.tuurdelacroix.careconnect.models.local

import java.time.LocalDate
import java.time.LocalTime

data class Medication(
    val types: List<MedicationType>,
    val name: String,
    val dose: String,
    var isTaken: Boolean = false,
    val schedule: Schedule


) {

}

data class Schedule(
    val date: LocalDate,
    val time: LocalTime
)
