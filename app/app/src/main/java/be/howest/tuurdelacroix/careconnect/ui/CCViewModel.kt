package be.howest.tuurdelacroix.careconnect.ui

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.howest.tuurdelacroix.careconnect.data.CCUiState
import be.howest.tuurdelacroix.careconnect.data.UserRepo
import be.howest.tuurdelacroix.careconnect.models.api.Contact
import be.howest.tuurdelacroix.careconnect.models.api.EventResponse
import be.howest.tuurdelacroix.careconnect.models.api.HeadCarer
import be.howest.tuurdelacroix.careconnect.models.api.Medication
import be.howest.tuurdelacroix.careconnect.models.api.PatientResponseAbstract
import be.howest.tuurdelacroix.careconnect.models.api.ShoppingItems
import be.howest.tuurdelacroix.careconnect.models.local.ShoppingItem
import be.howest.tuurdelacroix.careconnect.models.local.Task
import be.howest.tuurdelacroix.careconnect.network.CCApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

sealed interface CCAPIUiState {
    data class Success(
        val events: List<EventResponse>,
        val currentUser: PatientResponseAbstract,
        val headCarer: HeadCarer,
        var contacts: List<Contact>,
        var medication: List<Medication>,
        var medicationForToday: List<Medication>,
        var medicationForFuture: List<Medication>,
        var tasksForToday: List<be.howest.tuurdelacroix.careconnect.models.api.Task>,
        var tasksForFuture: List<be.howest.tuurdelacroix.careconnect.models.api.Task>

    ) : CCAPIUiState
    object Error : CCAPIUiState
    object Loading : CCAPIUiState

}

@RequiresApi(Build.VERSION_CODES.O)
class CCViewModel : ViewModel() {
    /* BEGIN MOCKDATA */
    private val _uiState = MutableStateFlow(
        CCUiState(
            currentUser = UserRepo.currentUser
        )
    )
    val uiState: StateFlow<CCUiState> = _uiState.asStateFlow()

    fun updateSelectedTask(task: Task)
    {
        _uiState.value.selectedTask = task
    }

    fun updateShoppingListOfSelectedTask(shoppingList: Map<ShoppingItem, Int>?)
    {
        _uiState.value.shoppingListOfSelectedTask = shoppingList
    }
   /* END MOCKDATA */

    var ccAPIUiState: CCAPIUiState by mutableStateOf(CCAPIUiState.Loading)
    var selectedTask: be.howest.tuurdelacroix.careconnect.models.api.Task? = null
    var shoppingListOfSelectedTask: List<ShoppingItems>? = null

    init {
        viewModelScope.launch {
            ccAPIUiState =
                try {
                    val events = getEvents()
                    val currentUser = getCurrentUser()
                    val headCarer = getHeadCarer()
                    val contacts = getContacts()
                    val medication = getMedication()
                    val medicationForToday = getMedicationForToday()
                    val medicationForFuture = getMedicationForFuture()
                    val tasksForToday = getTasksForToday()
                    val tasksForFuture = getTasksForFuture()
                    CCAPIUiState.Success(events, currentUser, headCarer, contacts, medication, medicationForToday, medicationForFuture, tasksForToday, tasksForFuture)
                } catch (ex: HttpException) {
                    Log.e("CCAPIUiState.Error", ex.response()?.toString()!!)
                    CCAPIUiState.Error
                }
        }
    }

    private suspend fun getEvents(): List<EventResponse> {
        return CCApi.retrofitService.getEvents()
    }

    private suspend fun getCurrentUser(): PatientResponseAbstract {
        return CCApi.retrofitService.getCurrentUser()
    }

    private suspend fun getHeadCarer(): HeadCarer {
        return CCApi.retrofitService.getHeadCarer()
    }

    private suspend fun getContacts(): List<Contact> {
        return CCApi.retrofitService.getContacts()
    }

    private suspend fun getMedication(): List<Medication> {
        return CCApi.retrofitService.getMedication()
    }
    private suspend fun getMedicationForToday(): List<Medication> {
        return CCApi.retrofitService.getMedicationForToday()
    }

    private suspend fun getMedicationForFuture(): List<Medication> {
        return CCApi.retrofitService.getMedicationForFuture()
    }

    private suspend fun getTasksForToday(): List<be.howest.tuurdelacroix.careconnect.models.api.Task> {
        return CCApi.retrofitService.getTasksForToday()
    }

    private suspend fun getTasksForFuture(): List<be.howest.tuurdelacroix.careconnect.models.api.Task> {
        return CCApi.retrofitService.getTasksForFuture()
    }

}