package be.howest.tuurdelacroix.careconnect.composables

import android.app.Activity
import android.os.Build
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import be.howest.tuurdelacroix.careconnect.R
import be.howest.tuurdelacroix.careconnect.data.*
import be.howest.tuurdelacroix.careconnect.models.api.EventResponse
import be.howest.tuurdelacroix.careconnect.models.api.HeadCarer
import be.howest.tuurdelacroix.careconnect.models.api.PatientResponseAbstract
import be.howest.tuurdelacroix.careconnect.ui.CCAPIUiState
import be.howest.tuurdelacroix.careconnect.ui.CCViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StartScreenCompos(
    viewModel: CCViewModel,
    uiState: CCUiState,
    modifier: Modifier,
    navController: NavController
)
{
    // Only if all data is received from the api, show the screen.
    val ccAPIUiState = viewModel.ccAPIUiState;
    when (ccAPIUiState) {
        is CCAPIUiState.Loading -> LoadingScreen(ccAPIUiState is CCAPIUiState.Success)
        is CCAPIUiState.Error -> ErrorScreen("Er heeft zich een fout voorgedaan. Probeer later opnieuw.", LocalContext.current)
        is CCAPIUiState.Success -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            ) {
                AllStartItems(currentUser = ccAPIUiState.currentUser, headCarer = ccAPIUiState.headCarer, ccAPIUiState.events, modifier, navController, ccAPIUiState)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AllStartItems(
    currentUser: PatientResponseAbstract,
    headCarer: HeadCarer,
    eventsToday: List<EventResponse>,
    modifier: Modifier,
    navController: NavController,
    uiState: CCAPIUiState.Success
) {
    UserInformation("${currentUser.firstName} ${currentUser.lastName}", headCarer.firstName!!, R.drawable.person_image)

    // Body
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CategoryTitle(R.string.cc_home_cat_events_title, modifier = Modifier)

        //val eventsToday = EventRepo.getEventsOfToday()

        Log.d("CC", eventsToday.toString())
        if (eventsToday.isNotEmpty())
        {
            UpcomingEventsCarousel(upcomingEvents = eventsToday)
        }
        else
        {
            NoContentToShow(R.string.cc_home_events_no_content_text, modifier)
        }
    }


    Spacer(modifier = Modifier.height(8.dp))

    NavigationMenu(modifier, navController)

    EmergencyButton(R.string.cc_home_emergency_button_text, uiState , modifier)
}

/**
 *
 */
@Composable
fun UserInformation(profileName: String, carerName: String, @DrawableRes profilePicture: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(profilePicture),
            contentDescription = "Person picture",
            modifier = Modifier
                .size(120.dp)
                .padding(8.dp)
                .clip(RoundedCornerShape(50)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = profileName,
            color = MaterialTheme.colors.primary,
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Verzorger: $carerName",
            color = Color.Gray,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
    Spacer(modifier = Modifier.height(32.dp))
}


/**
 *
 *  Composable that creates a navigation 'list' for all navigation buttons
 *
 */
@Composable
fun NavigationMenu(modifier: Modifier, navController: NavController) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CategoryTitle(R.string.cc_home_cat_nav_title, modifier)

        NavigationRepo.MenuNavItems.forEach { menuItem ->
            MenuNavItem(menuItem.label, menuItem.icon, menuItem.route.name, navController)
        }
    }
}

@Composable
fun MenuNavItem(
    title: String,
    icon: ImageVector,
    route: String,
    navController: NavController
) {
    Box(
        modifier = Modifier
            .padding(top = 4.dp, bottom = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colors.secondary),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clickable(onClick = {
                    navController.navigate(route)
                })
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
            )
        }
    }
}

/**
 * Composable that displays a emergency button
 *
 * @param textId contains the id for the text to show
 * @param modifier modifiers to set to this composable
 */
@Composable
fun EmergencyButton(textId: Int, uiState: CCAPIUiState.Success, modifier: Modifier) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier.padding(top = 32.dp)
    ) {
        Button(
            onClick = { showDialog = true },
            modifier = modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(CircleShape),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error)
        ) {
            Text(
                text = stringResource(textId),
                color = MaterialTheme.colors.secondary,
                fontSize = 20.sp
            )
        }
    }

    if (showDialog)
    {
        YesNoPopup(
            stringResource(R.string.cc_alert_emergency_button_title),
            stringResource(R.string.cc_alert_emergency_button_text),
            {
                sendEmergencyAlert(uiState, context as Activity)
                showDialog = false
            },
            {showDialog = false},
            {showDialog = false})
    }
}


/**
 * Composable that makes a horizontal carrousel to scroll thru the upcoming events.
 *
 *  @param upcomingEvents is a list of pairs (the events) that will be obtained with the API
 *
 */

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UpcomingEventsCarousel(upcomingEvents: List<EventResponse>) {
    var currentIndex by remember { mutableStateOf(0) }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { currentIndex = (currentIndex - 1).coerceAtLeast(0) },
                enabled = currentIndex > 0
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous")
            }

            val currEvent = upcomingEvents[currentIndex]
            UpcomingEvent(eventTitle = currEvent.title!!, eventTime = currEvent.date!!)

            IconButton(
                onClick = {
                    currentIndex = (currentIndex + 1).coerceAtMost(upcomingEvents.lastIndex)
                },
                enabled = currentIndex < upcomingEvents.lastIndex
            ) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next")
            }
        }

}

/**
 * Composable that creates an event item (visible)
 *
 *  @param eventTitle is the title of the event
 *  @param eventTime is the date of the event
 */

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UpcomingEvent(eventTitle: String, eventTime: LocalDateTime) {

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = eventTitle,
                style = MaterialTheme.typography.h6
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "Time",
                    tint = MaterialTheme.colors.onSurface
                )
                val formatter = DateTimeFormatter.ofPattern("HH:mm")
                Text(
                    text = eventTime.format(formatter),
                    style = MaterialTheme.typography.subtitle1
                )
            }
        }
    }
}

