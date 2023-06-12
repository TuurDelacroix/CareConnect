package be.howest.tuurdelacroix.careconnect

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import be.howest.tuurdelacroix.careconnect.ui.screens.CareConnectScreens
import be.howest.tuurdelacroix.careconnect.ui.screens.StartScreen
import be.howest.tuurdelacroix.careconnect.ui.theme.CareConnectTheme
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CareConnectUITests {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var navController: TestNavHostController

    @Before
    fun setupCCNavHost() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            CareConnectTheme {
                StartScreen(navController = navController, localRoomDb = null)
            }
        }
    }

    @Test
    fun ccNavHost_verifyStartScreen()
    {
        navController.assertCurrentRouteName(CareConnectScreens.Start.name)
    }

    @Test
    fun ccNavHost_navigateToMedicationScreen() {
        navigateToMedicationScreen()
        navController.assertCurrentRouteName(CareConnectScreens.Medication.name)
    }

    @Test
    fun ccNavHost_navigateToToDoScreen() {
        navigateToToDoScreen()
        navController.assertCurrentRouteName(CareConnectScreens.ToDo.name)
    }

    @Test
    fun ccNavHost_navigateToCarerScreen() {
        navigateToCarerScreen()
        navController.assertCurrentRouteName(CareConnectScreens.Carer.name)
    }

    @Test
    fun ccNavHost_navigateToEditTaskScreen() {
        navigateToTaskEditScreen()
        navController.assertCurrentRouteName(CareConnectScreens.Edit.name)
    }

    @Test
    fun ccNavHost_navigateToShoppingListTaskScreen() {
        navigateToShoppingListTaskScreen()
        navController.assertCurrentRouteName(CareConnectScreens.ShoppingList.name)
    }

    private fun navigateToMedicationScreen() {
        composeTestRule.runOnUiThread {
            navController.navigate(CareConnectScreens.Medication.name)
        }
    }

    private fun navigateToToDoScreen() {
        composeTestRule.runOnUiThread {
            navController.navigate(CareConnectScreens.ToDo.name)
        }
    }

    private fun navigateToCarerScreen() {
        composeTestRule.runOnUiThread {
            navController.navigate(CareConnectScreens.Carer.name)
        }
    }

    private fun navigateToTaskEditScreen()
    {
        composeTestRule.runOnUiThread {
            navController.navigate(CareConnectScreens.Edit.name)
        }
    }

    private fun navigateToShoppingListTaskScreen()
    {
        composeTestRule.runOnUiThread {
            navController.navigate(CareConnectScreens.ShoppingList.name)
        }
    }


}

// Test
fun NavController.assertCurrentRouteName(expectedRouteName: String) {
    assertEquals(expectedRouteName, currentBackStackEntry?.destination?.route)
}