package be.howest.tuurdelacroix.careconnect.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import be.howest.tuurdelacroix.careconnect.R
import be.howest.tuurdelacroix.careconnect.composables.ActionButton
import be.howest.tuurdelacroix.careconnect.composables.CategoryTitle
import be.howest.tuurdelacroix.careconnect.composables.ErrorScreen
import be.howest.tuurdelacroix.careconnect.composables.LoadingScreen
import be.howest.tuurdelacroix.careconnect.composables.NoContentToShow
import be.howest.tuurdelacroix.careconnect.composables.PageInfoCard
import be.howest.tuurdelacroix.careconnect.data.CCUiState
import be.howest.tuurdelacroix.careconnect.data.showToastAlert
import be.howest.tuurdelacroix.careconnect.models.local.TaskStatus
import be.howest.tuurdelacroix.careconnect.ui.CCAPIUiState
import be.howest.tuurdelacroix.careconnect.ui.CCViewModel
import compose.icons.LineAwesomeIcons
import compose.icons.lineawesomeicons.HourglassSolid
import compose.icons.lineawesomeicons.ListSolid
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskScreen(
    viewModel: CCViewModel,
    uiState: CCUiState,
    navController: NavHostController,
    modifier: Modifier
) {

    val ccAPIUiState = viewModel.ccAPIUiState
    val context = LocalContext.current
    when (ccAPIUiState) {
        is CCAPIUiState.Loading -> LoadingScreen(ccAPIUiState is CCAPIUiState.Success)
        is CCAPIUiState.Error -> ErrorScreen("Er heeft zich een fout voorgedaan. Probeer later opnieuw.", LocalContext.current)
        is CCAPIUiState.Success -> {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(MaterialTheme.colors.background)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    PageInfoCard(icon = LineAwesomeIcons.ListSolid, title = R.string.cc_todo_screen_title)

                    //val tasksForToday = uiState.currentUser.tasksForToday
                    //val tasksForFuture = uiState.currentUser.tasksForFuture
                    //val tasksForToday by UserRepo.getTasksForTodayFlow().collectAsState(emptyList())
                    //val tasksForFuture by UserRepo.getTasksForFutureFlow().collectAsState(emptyList())

                    val tasksForToday = ccAPIUiState.tasksForToday
                    val tasksForFuture = ccAPIUiState.tasksForFuture

                    TaskList(tasksForToday, tasksForFuture, navController, uiState, viewModel)
                }


                ActionButton(
                    onClickAction = {
                        /*TODO OUT OF SCOPE */
                        showToastAlert(context, "BINNENKORT!")
                    },
                    buttonColor = R.color.light_green,
                    buttonTextColor = MaterialTheme.colors.secondary,
                    buttonText = R.string.cc_task_newtask_button_text,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }


        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskList(
    taskListToday: List<be.howest.tuurdelacroix.careconnect.models.api.Task>,
    taskListFuture: List<be.howest.tuurdelacroix.careconnect.models.api.Task>,
    navController: NavHostController,
    uiState: CCUiState,
    viewModel: CCViewModel
)
{
    CategoryTitle(text = R.string.cc_task_cat_today_title, modifier = Modifier)

    if (taskListToday.isNotEmpty())
    {
        Box(modifier = Modifier.height(160.dp)) {
            LazyColumn {
                items(taskListToday) { task ->
                    TaskView(task, navController, uiState, viewModel)
                }
            }
        }
    }
    else {
        NoContentToShow(R.string.cc_task_no_content_today_screen_text, modifier = Modifier.fillMaxWidth())
    }


    CategoryTitle(text = R.string.cc_task_cat_future_title, modifier = Modifier)

    if (taskListFuture.isNotEmpty()) {
        Box(modifier = Modifier.height(160.dp)) {
            LazyColumn {
                items(taskListFuture) { task ->
                    TaskView(task, navController, uiState, viewModel)
                }
            }
        }
    } else {
        NoContentToShow(R.string.cc_task_no_content_future_screen_text, modifier = Modifier.fillMaxWidth())
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskView(task: be.howest.tuurdelacroix.careconnect.models.api.Task, navController: NavController, uiState: CCUiState, viewModel: CCViewModel)
{
    val taskBackgroundColor = when (task.status)
    {
        TaskStatus.COMPLETED -> Color.Gray
        TaskStatus.IN_PROGRESS -> Color.LightGray
        else -> MaterialTheme.colors.secondary
    }

    val taskContentColor = when(task.status)
    {
        TaskStatus.COMPLETED -> MaterialTheme.colors.secondary
        else -> MaterialTheme.colors.primary
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(taskBackgroundColor),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Column(
                Modifier.weight(1f)
            ) {
                Icon(
                    task.type!!.icon,
                    contentDescription = task.title,
                    Modifier.size(48.dp),
                    tint = taskContentColor
                )
            }

            Column(
                Modifier.weight(2f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
            ) {
                val formatter = DateTimeFormatter.ofPattern("d MMMM")
                Text(text = task.title!!, fontWeight = FontWeight.Bold, color = taskContentColor)
                Text(text = task.date!!.format(formatter), fontSize = 12.sp, color = taskContentColor)
            }

            Column(
                Modifier.weight(2f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End,
            ) {
                Text(text = "Status", fontWeight = FontWeight.Bold, color = taskContentColor)
                Text(text = task.status!!.label, fontSize = 12.sp, color = taskContentColor)
            }

            Column(
                Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End,
            ) {
                if (task.status == TaskStatus.REQUESTED && task.date!!.toLocalDate() > LocalDate.now())
                {
                    Icon(
                        task.status!!.icon,
                        contentDescription = task.status!!.label,
                        Modifier
                            .size(32.dp)
                            .clickable(onClick = {
                                //viewModel.updateShoppingListOfSelectedTask(task.shoppingList)
                                viewModel.selectedTask = task
                                navController.navigate(CareConnectScreens.Edit.name)
                            }),
                        tint = taskContentColor
                    )
                }
                else if (task.status == TaskStatus.COMPLETED)
                {
                    Icon(
                        task.status!!.icon,
                        contentDescription = task.status!!.label,
                        Modifier
                            .size(32.dp),
                        tint = taskContentColor
                    )
                }
                else
                {
                    Icon(
                        LineAwesomeIcons.HourglassSolid,
                        contentDescription = task.status!!.label,
                        Modifier
                            .size(32.dp),
                        tint = taskContentColor
                    )
                }

            }
        }
    }
}