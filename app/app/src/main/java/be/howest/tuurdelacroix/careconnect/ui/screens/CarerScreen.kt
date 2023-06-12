package be.howest.tuurdelacroix.careconnect.ui.screens

import android.app.Activity
import android.os.Build
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import be.howest.tuurdelacroix.careconnect.R
import be.howest.tuurdelacroix.careconnect.composables.CategoryTitle
import be.howest.tuurdelacroix.careconnect.composables.ErrorScreen
import be.howest.tuurdelacroix.careconnect.composables.LoadingScreen
import be.howest.tuurdelacroix.careconnect.composables.PageTitleCard
import be.howest.tuurdelacroix.careconnect.composables.RoundedCard
import be.howest.tuurdelacroix.careconnect.data.*
import be.howest.tuurdelacroix.careconnect.network.CCApi
import be.howest.tuurdelacroix.careconnect.ui.CCAPIUiState
import be.howest.tuurdelacroix.careconnect.ui.CCViewModel
import compose.icons.LineAwesomeIcons
import compose.icons.lineawesomeicons.ExclamationCircleSolid
import compose.icons.lineawesomeicons.HandsHelpingSolid
import compose.icons.lineawesomeicons.ListSolid
import compose.icons.lineawesomeicons.PhoneSolid
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CarerScreen(
    viewModel: CCViewModel,
    uiState: CCUiState,
    navController: NavHostController,
    modifier: Modifier
) {

    val ccAPIUiState = viewModel.ccAPIUiState;
    when (ccAPIUiState) {
        is CCAPIUiState.Loading -> LoadingScreen(ccAPIUiState is CCAPIUiState.Success)
        is CCAPIUiState.Error -> ErrorScreen("Er heeft zich een fout voorgedaan. Probeer later opnieuw.", LocalContext.current)
        is CCAPIUiState.Success -> {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(MaterialTheme.colors.background),
                horizontalAlignment = Alignment.CenterHorizontally
            )

            {
                PageTitleCard(
                    icon = LineAwesomeIcons.ListSolid,
                    stringResource(
                        R.string.cc_carer_screen_title
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                CarerInformation("${ccAPIUiState.headCarer.firstName} ${ccAPIUiState.headCarer.lastName}", "Hoofdverzorger", R.drawable.carer_image)

                ActionButtons(uiState, ccAPIUiState , modifier)

                Spacer(modifier = Modifier.height(8.dp))

                CategoryTitle(R.string.cc_carer_enablement_title, modifier)

                ContactList(uiState, ccAPIUiState, modifier, viewModel)

                Spacer(modifier = Modifier.height(8.dp))

                SOSText(modifier)

            }

        }
    }

}

@Composable
fun ActionButtons(uiState: CCUiState, ccAPIUiState: CCAPIUiState.Success, modifier: Modifier)
{
    val context = LocalContext.current

    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    )
    {
        RoundedCard(
            icon = LineAwesomeIcons.PhoneSolid,
            title = R.string.cc_carer_actionbutton_call
        ) { callHeadCarer(ccAPIUiState, context as Activity) }
        RoundedCard(
            icon = LineAwesomeIcons.HandsHelpingSolid,
            title = R.string.cc_carer_actionbutton_visit
        ) { sendAccompanyTextMessage(ccAPIUiState, context as Activity) }
        RoundedCard(
            icon = LineAwesomeIcons.ExclamationCircleSolid,
            title = R.string.cc_carer_actionbutton_sos,
        ) { sendUrgentInviteTextMessage(ccAPIUiState, context as Activity) }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ContactList(
    uiState: CCUiState,
    ccAPIUiState: CCAPIUiState.Success,
    modifier: Modifier,
    viewModel: CCViewModel
)
{
    //val contacts by UserRepo.getContactsFlow().collectAsState(emptyList())
    val contacts = ccAPIUiState.contacts

    LazyColumn {
        items(contacts) { contact ->
            Row(
                modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${contact.firstName} ${contact.lastName}",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier.width(8.dp))
                Switch(
                    checked = contact.reachable!!,
                    onCheckedChange = { isChecked ->
                        //todo change to api call
                        //UserRepo.toggleContactReachable(uiState, contact)
                        viewModel.viewModelScope.launch {
                            try {
                                val updatedContact = CCApi.retrofitService.toggleContactReachable(contact.id!!)
                                val updatedContacts = ccAPIUiState.contacts.map {
                                    if (it.id == updatedContact.id) updatedContact
                                    else it
                                }

                                viewModel.ccAPIUiState = ccAPIUiState.copy(contacts = updatedContacts)

                            } catch (ex: Exception)
                            {
                                Log.e("CC", ex.toString())
                            }
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colorResource(R.color.light_green),
                        checkedTrackColor = colorResource(R.color.slightdarker_green),
                        uncheckedThumbColor = MaterialTheme.colors.error,
                        uncheckedTrackColor = colorResource(R.color.slightdarker_red),
                    ),
                    modifier = Modifier.height(24.dp)
                )
            }
        }
    }
}


@Composable
fun CarerInformation(carerName: String, subtitle: String, @DrawableRes profilePicture: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .padding(16.dp),
    ) {
        Image(
            painter = painterResource(profilePicture),
            contentDescription = "Person picture",
            modifier = Modifier
                .size(100.dp)
                .padding(4.dp)
                .clip(RoundedCornerShape(50)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = carerName,
            color = MaterialTheme.colors.primary,
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = subtitle,
            color = Color.Gray,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
fun SOSText(modifier: Modifier)
{
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        Text(
            stringResource(R.string.cc_carer_sos_text),
            fontWeight = FontWeight.Normal
        )
        Text(
            stringResource(R.string.cc_carer_sos_112),
            fontWeight = FontWeight.Bold
        )
    }
}