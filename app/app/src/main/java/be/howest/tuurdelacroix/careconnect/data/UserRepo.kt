package be.howest.tuurdelacroix.careconnect.data

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import be.howest.tuurdelacroix.careconnect.R
import be.howest.tuurdelacroix.careconnect.models.*
import be.howest.tuurdelacroix.careconnect.models.local.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
object UserRepo {

    private val appUsers = mutableListOf<User>()
    private val contacts = MutableStateFlow<List<Contact>>(emptyList())
    var medication = MutableStateFlow<List<Medication>>(emptyList())
    var tasksForToday = MutableStateFlow<List<Task>>(emptyList())
    var tasksForFuture = MutableStateFlow<List<Task>>(emptyList())

    val headCarer = User("Tuur", "Delacroix", R.drawable.carer_image, UserType.HEAD_CARER, "+320471448187")
    val currentUser: Patient
        get() = createCurrentUser()

    private fun createCurrentUser(): Patient {
        return Patient("Annie", "Biljet", R.drawable.person_image, "+320473607375", headCarer, contacts.value, medication.value, tasksForToday.value, tasksForFuture.value)
    }

    init {
        contacts.value =
            listOf(
                Contact("Danny", "Delacroix", null, "+320479289525", true),
                Contact("Brian", "Bota", null, "+320473288349")
            )

        medication.value = MedicationRepo.medicationList

        tasksForToday.value = TaskRepo.getTasksForToday()
        tasksForFuture.value = TaskRepo.getFutureTasks()

        appUsers.addAll(
            listOf(
                headCarer,
                currentUser
            )
        )
    }

    // Patient Global

    fun getContactsFlow(): StateFlow<List<Contact>> {
        return contacts
    }

    fun toggleContactReachable(uiState: CCUiState, contact: Contact) {
        val updatedContacts = contacts.value.map { ctct ->
            if (ctct == contact) {
                ctct.copy(reachable = !ctct.reachable)
            } else {
                ctct
            }
        }
        contacts.value = updatedContacts
        uiState.updateCurrentUserContactList(updatedContacts)
    }

    // Medication
    fun toggleMedicationState(uiState: CCUiState, selectedMedication: Medication, context: Context)
    {
        uiState.toggleMedicationTakenState(selectedMedication)
        showToastAlert(context, context.getString(R.string.cc_toast_alert_medication_single_taken_text))
    }

    fun markAllMedicationAsTaken(uiState: CCUiState, context: Context)
    {
        uiState.markAllMedicationAsTaken()
        showToastAlert(context, context.getString(R.string.cc_toast_alert_medication_all_taken_text))
    }

    // Tasks

    fun getTasksForTodayFlow(): MutableStateFlow<List<Task>> {
        return tasksForToday
    }

    fun getTasksForFutureFlow(): MutableStateFlow<List<Task>> {
        return tasksForFuture
    }

    fun updateShoppingList(task: Task, shoppingList: List<ShoppingItem>)
    {

    }

    fun updateDate(uiState: CCUiState, task: Task, newDate: LocalDate)
    {
        // Apply the new date to the task.
        Log.d("CC", "TASK BEFORE: $task")
        val updatedTask = task.copy(date = LocalDateTime.of(newDate.year, newDate.month, newDate.dayOfMonth, task.date.hour, task.date.minute))
        Log.d("CC", "updated task: $updatedTask")

        val updatedTaskListForToday = uiState.currentUser.tasksForToday.map {tsk->
            if (tsk == task)
            {
                updatedTask
            } else {
                tsk
            }
        }

        val updatedTaskListForFuture = uiState.currentUser.tasksForFuture.map {tsk ->
            if (tsk == task)
            {
                updatedTask
            } else {
                tsk
            }
        }

        //Log.d("CC UPDATES TASKLIST", updatedTaskListForFuture.toString())

        /*
        *
        * Updating the tasks for today is not that necessary cuz i made it only possible for the user
        * to change tasks that are not from the same day. But for future purpose (out of scope for now)
        * the carer will be able to change the tasks status and the same method will be used for this.
        *
        */

        tasksForToday.value = updatedTaskListForToday
        tasksForFuture.value = updatedTaskListForFuture

        uiState.updateTasks(updatedTaskListForToday, updatedTaskListForFuture)
        uiState.selectedTask = updatedTask

    }

    // ShoppingList
    fun increaseProductAmount(uiState: CCUiState, product: ShoppingItem) {

        uiState.shoppingListOfSelectedTask?.let { shoppingList ->
            val currentAmount = shoppingList[product] ?: 0
            uiState.shoppingListOfSelectedTask = shoppingList + (product to (currentAmount + 1))
        }

        val currentTask = uiState.selectedTask
        val updatedTask = currentTask!!.copy(shoppingList = uiState.shoppingListOfSelectedTask)

        val updatedTaskList = currentUser.tasksForFuture.map { tsk ->
            if (tsk == uiState.selectedTask)
            {
                updatedTask
            }
            else {
                tsk
            }
        }

        uiState.updateTasks(tasksForToday.value, updatedTaskList)
        uiState.selectedTask = updatedTask
        uiState.shoppingListOfSelectedTask = updatedTask.shoppingList
    }

    fun decreaseProductAmount(uiState: CCUiState, product: ShoppingItem) {
        uiState.shoppingListOfSelectedTask?.let { shoppingList ->
            val currentAmount = shoppingList[product] ?: 0
            if (currentAmount == 0) return
            uiState.shoppingListOfSelectedTask = shoppingList + (product to (currentAmount -1))
        }

        val currentTask = uiState.selectedTask
        val updatedTask = currentTask!!.copy(shoppingList = uiState.shoppingListOfSelectedTask)

        val updatedTaskList = currentUser.tasksForFuture.map { tsk ->
            if (tsk == uiState.selectedTask)
            {
                updatedTask
            }
            else {
                tsk
            }
        }

        uiState.updateTasks(tasksForToday.value, updatedTaskList)
        uiState.selectedTask = updatedTask
        uiState.shoppingListOfSelectedTask = updatedTask.shoppingList
    }

    fun removeProduct(uiState: CCUiState, product: ShoppingItem, context: Context)
    {
        Log.d("CC PRDCT BEFORE DELETE", product.toString())
        Log.d("CC LIST BEFORE DELETE", uiState.shoppingListOfSelectedTask.toString())
        uiState.shoppingListOfSelectedTask?.let { shoppingList ->
            val updatedShoppingList = shoppingList.filter { it.key != product }
            uiState.shoppingListOfSelectedTask = updatedShoppingList
        }

        val currentTask = uiState.selectedTask
        val updatedTask = currentTask!!.copy(shoppingList = uiState.shoppingListOfSelectedTask)

        val updatedTaskList = currentUser.tasksForFuture.map { tsk ->
            if (tsk == uiState.selectedTask) {
                updatedTask
            } else {
                tsk
            }
        }

        uiState.updateTasks(tasksForToday.value, updatedTaskList)
        uiState.selectedTask = updatedTask
        uiState.shoppingListOfSelectedTask = updatedTask.shoppingList

        showToastAlert(context, context.getString(R.string.cc_toast_alert_shoppinglist_removed_product_text))

        Log.d("CC DELETE AFTER", uiState.shoppingListOfSelectedTask.toString())
        Log.d("CC SELECTED TASK AFTER DELETE", uiState.selectedTask.toString())
    }

    fun addProduct(uiState: CCUiState, product: ShoppingItem, context: Context)
    {
        uiState.shoppingListOfSelectedTask?.let { shoppingList ->
            if (shoppingList.containsKey(product)) {
                val currentAmount = shoppingList[product]!!
                uiState.shoppingListOfSelectedTask = shoppingList.plus(product to currentAmount + 1)
            } else {
                uiState.shoppingListOfSelectedTask = shoppingList.plus(product to 1)
            }
        }

        val currentTask = uiState.selectedTask
        val updatedTask = currentTask!!.copy(shoppingList = uiState.shoppingListOfSelectedTask)

        val updatedTaskList = currentUser.tasksForFuture.map { tsk ->
            if (tsk == uiState.selectedTask) {
                updatedTask
            } else {
                tsk
            }
        }

        uiState.updateTasks(tasksForToday.value, updatedTaskList)
        uiState.selectedTask = updatedTask
        uiState.shoppingListOfSelectedTask = updatedTask.shoppingList

        showToastAlert(context, context.getString(R.string.cc_toast_alert_shoppinglist_added_product_text, product.name))
    }
}