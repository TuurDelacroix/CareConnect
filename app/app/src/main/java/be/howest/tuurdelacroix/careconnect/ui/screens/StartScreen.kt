package be.howest.tuurdelacroix.careconnect.ui.screens

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import be.howest.tuurdelacroix.careconnect.R
import be.howest.tuurdelacroix.careconnect.composables.StartScreenCompos
import be.howest.tuurdelacroix.careconnect.data.NavigationRepo
import be.howest.tuurdelacroix.careconnect.localroom.CCDatabase
import be.howest.tuurdelacroix.careconnect.ui.CCAPIUiState
import be.howest.tuurdelacroix.careconnect.ui.CCViewModel

enum class CareConnectScreens(@StringRes val title: Int) {
    // Enum class representing the different screens in the app
    Start(title = R.string.cc_nav_start),
    Medication(title = R.string.cc_nav_medication),
    ToDo(title = R.string.cc_nav_todo),
    Carer(title = R.string.cc_nav_carer),
    Edit(title = R.string.cc_task_edit),
    ShoppingList(title=R.string.cc_shopl_overview),
    AddProduct(title=R.string.cc_addproduct),
    Camera(R.string.cc_camera)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StartScreen(
    localRoomDb: CCDatabase?,
    modifier: Modifier = Modifier,
    navController: NavHostController) {
    // ViewModel for the screen
    val viewModel: CCViewModel = viewModel()

    // Get the current screen from the back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = CareConnectScreens.valueOf(
        backStackEntry?.destination?.route ?: CareConnectScreens.Start.name
        )

    // Observe the UI state using Flow.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    //val uiApiState = viewModel.ccAPIUiState;

    Scaffold(
        topBar = {
            // Show different top bars based on the current screen
            if (currentScreen.name == CareConnectScreens.Start.name)
            {
                TopBarWithIcon(appIcon = R.drawable.cc_app_logo, modifier)
            }
            else
            {
                val currentUser = (viewModel.ccAPIUiState as CCAPIUiState.Success).currentUser
                TopBarWithName(currentUser = currentUser.firstName!!, appIcon = R.drawable.cc_app_logo, modifier)
            }
        },
        bottomBar = {
            // Show bottom navigation for all screens except the Start screen
            if (currentScreen.name != CareConnectScreens.Start.name) {
                BottomNavigation(
                    modifier = Modifier
                        .height(65.dp),
                    backgroundColor = colorResource(id = R.color.white)
                ) {
                    // Iterate through the bottom navigation items and create a BottomNavigationItem for each
                    NavigationRepo.BottomNavItems.forEach { navItem ->
                        BottomNavigationItem(
                            selected = currentScreen == navItem.route,
                            onClick = {
                                navController.navigate(navItem.route.name)
                            },
                            icon = {
                                Column(modifier = Modifier.padding(4.dp))
                                {
                                    Icon(
                                        navItem.icon,
                                        contentDescription = navItem.label,
                                        modifier = modifier
                                            .size(30.dp)
                                            .align(Alignment.CenterHorizontally)
                                    )
                                    Text(
                                        text = navItem.label,
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = CareConnectScreens.Start.name,
            modifier = modifier
                .padding(innerPadding)
                .background(MaterialTheme.colors.background)
        ) {
            // Define composable functions for each screen

            composable(route = CareConnectScreens.Start.name) {
                StartScreenCompos(viewModel, uiState, modifier, navController)
            }
            composable(route = CareConnectScreens.Medication.name) {
                MedicationScreen(
                    viewModel,
                    uiState,
                    navController,
                    modifier
                )
            }
            composable(route = CareConnectScreens.ToDo.name) {
                TaskScreen(
                    viewModel,
                    uiState,
                    navController,
                    modifier
                )
            }
            composable(route = CareConnectScreens.Carer.name) {
                CarerScreen(
                    viewModel,
                    uiState,
                    navController,
                    modifier
                )
            }
            composable(route = CareConnectScreens.Edit.name) {
                EditTaskScreen(
                    viewModel,
                    uiState,
                    navController,
                    modifier,
                )
            }
            composable(route = CareConnectScreens.ShoppingList.name) {
                ShoppingListScreen(
                    viewModel,
                    uiState,
                    navController,
                    modifier,
                    localRoomDb!!
                )
            }
            composable(route = CareConnectScreens.AddProduct.name) {
                AddProductScreen(
                    viewModel,
                    uiState,
                    navController,
                    modifier,
                    localRoomDb!!
                )
            }
        }
    }
}


/**
 * Composable that displays a top bar with the icon of the app.
 *
 * @param appIcon contains the id of the app icon
 * @param modifier modifiers to set to this composable
 */
@Composable
fun TopBarWithIcon(@DrawableRes appIcon: Int, modifier: Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.secondary)
            .padding(horizontal = 16.dp, vertical = 8.dp),

    ) {
        Image(
            modifier = Modifier
                .size(64.dp)
                .padding(8.dp),
            painter = painterResource(appIcon),
            contentDescription = null

        )

    }
}

@Composable
fun TopBarWithName(currentUser: String, @DrawableRes appIcon: Int, modifier: Modifier)
{
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.secondary)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = currentUser,
            style = MaterialTheme.typography.h6,
            modifier = Modifier.weight(4f)
        )
        Image(
            painter = painterResource(appIcon),
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .padding(8.dp)
        )
    }
}