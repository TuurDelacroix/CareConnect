package be.howest.tuurdelacroix.careconnect.data

import android.os.Build
import androidx.annotation.RequiresApi
import be.howest.tuurdelacroix.careconnect.models.*
import be.howest.tuurdelacroix.careconnect.models.local.*
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
data class CCUiState(

    val currentUser: Patient = UserRepo.currentUser,
    //val currentUser2: PatientResponseAbstract = runBlocking { CCApi.retrofitService.getCurrentUser() },
    val headCarer: User = currentUser.headCarer,
    //val headCarer2: HeadCarer =  runBlocking { CCApi.retrofitService.getHeadCarer() },

    var selectedTask: Task? = null,
    var shoppingListOfSelectedTask: Map<ShoppingItem, Int>? = null,

) {
    /* BEGIN MOCKDATA */
    fun updateCurrentUserContactList(updatedContacts: List<Contact>)
    {
        currentUser.contacts = updatedContacts
    }

    private fun updateCurrentUserMedicationList(updatedMedicationList: List<Medication>)
    {
        currentUser.medication = updatedMedicationList
        UserRepo.medication.value = updatedMedicationList
    }

    fun toggleMedicationTakenState(selectedMedication: Medication) {
    val updatedMedication = currentUser.medication.map { med ->
        if (med == selectedMedication) {
            med.copy(isTaken = !med.isTaken)
        } else {
            med
        }
    }

    currentUser.medication = updatedMedication
    updateCurrentUserMedicationList(updatedMedication)
    }

    fun markAllMedicationAsTaken()
    {
    currentUser.medication.forEach { medication ->
        if (medication.schedule.date == LocalDate.now())
        {
            medication.isTaken = true
        }
    }

    updateCurrentUserMedicationList(currentUser.medication)
    }

    // Tasks

    fun updateTasks(updatedTaskListForToday: List<Task>, updatedTaskListForFuture: List<Task>) {

    currentUser.tasksForToday = updatedTaskListForToday
    currentUser.tasksForFuture = updatedTaskListForFuture

    }
    /*  END MOCKDATA */
}
// todo finetune this user data class usage?