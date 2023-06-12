package be.howest.tuurdelacroix.careconnect.data

import android.os.Build
import androidx.annotation.RequiresApi
import be.howest.tuurdelacroix.careconnect.models.local.Medication
import be.howest.tuurdelacroix.careconnect.models.local.MedicationType
import be.howest.tuurdelacroix.careconnect.models.local.Schedule
import java.time.*

@RequiresApi(Build.VERSION_CODES.O)
object MedicationRepo {

    val medicationList = mutableListOf<Medication>()

    init {
        medicationList.addAll(
            listOf(
                Medication(
                    listOf(MedicationType.PIL),
                    "LUCOVITAAL: Mentale Focus",
                    "1 tablet",
                    false,
                    Schedule(LocalDate.of(2023, 4, 17), LocalTime.of(10,0))
                ),
                Medication(
                    listOf(MedicationType.BRUISTABLET, MedicationType.KAUWTABLET),
                    "Dafalgan Forte",
                    "500 mg",
                    true,
                    Schedule(LocalDate.of(2023, 4, 17), LocalTime.of(23,0))
                ),
                Medication(
                    listOf(MedicationType.PIL),
                    "Antimetil",
                    "1 capsule",
                    false,
                    Schedule(LocalDate.of(2023,4, 18), LocalTime.of(23,0))
                ),
            )
        )
    }
}