package be.howest.tuurdelacroix.careconnect.ui.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import be.howest.tuurdelacroix.careconnect.R
import be.howest.tuurdelacroix.careconnect.composables.ActionButton
import be.howest.tuurdelacroix.careconnect.composables.BackButton
import be.howest.tuurdelacroix.careconnect.composables.CategoryTitle
import be.howest.tuurdelacroix.careconnect.composables.ErrorScreen
import be.howest.tuurdelacroix.careconnect.composables.LoadingScreen
import be.howest.tuurdelacroix.careconnect.composables.PageTitleCard
import be.howest.tuurdelacroix.careconnect.data.CCUiState
import be.howest.tuurdelacroix.careconnect.models.api.Task
import be.howest.tuurdelacroix.careconnect.models.local.TaskType
import be.howest.tuurdelacroix.careconnect.network.CCApi
import be.howest.tuurdelacroix.careconnect.network.CareConnectAPIService
import be.howest.tuurdelacroix.careconnect.ui.CCAPIUiState
import be.howest.tuurdelacroix.careconnect.ui.CCViewModel
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import compose.icons.LineAwesomeIcons
import compose.icons.lineawesomeicons.ListSolid
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditTaskScreen(
    viewModel: CCViewModel,
    uiState: CCUiState,
    navController: NavHostController,
    modifier: Modifier
) {
    val selectedTask = viewModel.selectedTask

    val ccAPIUiState = viewModel.ccAPIUiState
    when (ccAPIUiState) {
        is CCAPIUiState.Loading -> LoadingScreen(ccAPIUiState is CCAPIUiState.Success)
        is CCAPIUiState.Error -> ErrorScreen("Er heeft zich een fout voorgedaan. Probeer later opnieuw.", LocalContext.current)
        is CCAPIUiState.Success -> {

            Column(
                modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(MaterialTheme.colors.background),
                horizontalAlignment = Alignment.CenterHorizontally
            )

            {
                BackButton { navController.navigate(CareConnectScreens.ToDo.name) }

                PageTitleCard(icon = LineAwesomeIcons.ListSolid,
                    stringResource(
                        R.string.cc_edit_task_screen_title,
                        selectedTask?.type?.label?.uppercase() ?: "",
                        selectedTask?.title ?: ""
                    )
                )

                TaskEditForm(selectedTask, modifier, ccAPIUiState, navController, viewModel)

                ActionButton(
                    onClickAction = { navController.navigate(CareConnectScreens.ToDo.name) },
                    buttonColor = R.color.light_green,
                    buttonTextColor = MaterialTheme.colors.secondary,
                    buttonText = R.string.cc_taskedit_savetask_button_text,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }

        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskEditForm(
    selectedTask: Task?,
    modifier: Modifier,
    uiState: CCAPIUiState.Success,
    navController: NavHostController,
    viewModel: CCViewModel
)
{
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CategoryTitle(text = R.string.cc_taskedit_cat_date_title, modifier = modifier)

        val calenderState = rememberUseCaseState()
        var selectedTaskDateState by remember { mutableStateOf(selectedTask?.date)}

        CalendarDialog(
            state = calenderState,
            config = CalendarConfig(
                monthSelection = true,
                yearSelection = false,
                style = CalendarStyle.WEEK,
                boundary = (LocalDate.now()..LocalDate.now().plusMonths(1))
            ),
            selection = CalendarSelection.Date {
                date ->
                viewModel.viewModelScope.launch {
                    try {

                        val updatedTaskListForFuture = CCApi.retrofitService.updateTaskDate(selectedTask?.id!!, CareConnectAPIService.TaskUpdateRequest(date.toString()))
                        viewModel.selectedTask = CCApi.retrofitService.getTaskWithId(selectedTask.id!!)
                        viewModel.ccAPIUiState = uiState.copy(tasksForFuture = updatedTaskListForFuture)
                        selectedTaskDateState = viewModel.selectedTask!!.date

                    } catch (ex: Exception)
                    {
                        Log.e("CC", ex.toString())
                    }
                }
                //UserRepo.updateDate(uiState, selectedTask!!, date)
                //selectedTaskDateState = uiState.selectedTask?.date

        })

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        selectedTaskDateState?.format(formatter)?.let {
                Box(
                    modifier
                        .background(MaterialTheme.colors.secondary, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                        .clickable(onClick = {
                            calenderState.show()
                        })
                ) {
                    Text(text = it)
                }
        }

        if (selectedTask?.type == TaskType.DO_SHOPPING)
        {
            CategoryTitle(text = R.string.cc_taskedit_cat_shoppinglist_title, modifier = modifier)

            Box(
                modifier
                    .background(MaterialTheme.colors.secondary, RoundedCornerShape(16.dp))
                    .padding(16.dp)
                    .clickable(onClick = {

                        viewModel.viewModelScope.launch {

                            try {

                                val shoppingListOfTask = CCApi.retrofitService.getTaskShoppingListItems(selectedTask.id!!)
                                Log.d("CC", shoppingListOfTask.toString())
                                viewModel.shoppingListOfSelectedTask = shoppingListOfTask

                                navController.navigate(CareConnectScreens.ShoppingList.name)


                            } catch (ex: Exception)
                            {
                                Log.e("CC", ex.toString())
                            }

                        }


                    })
            ) {
                Text(text = "Wijzig Winkelwagen")
            }
        }


    }
}
