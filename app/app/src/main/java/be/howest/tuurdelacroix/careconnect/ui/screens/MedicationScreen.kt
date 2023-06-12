package be.howest.tuurdelacroix.careconnect.ui.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import be.howest.tuurdelacroix.careconnect.R
import be.howest.tuurdelacroix.careconnect.composables.ActionButton
import be.howest.tuurdelacroix.careconnect.composables.ErrorScreen
import be.howest.tuurdelacroix.careconnect.composables.LoadingScreen
import be.howest.tuurdelacroix.careconnect.composables.NoContentToShow
import be.howest.tuurdelacroix.careconnect.composables.PageInfoCard
import be.howest.tuurdelacroix.careconnect.composables.YesNoPopup
import be.howest.tuurdelacroix.careconnect.data.CCUiState
import be.howest.tuurdelacroix.careconnect.network.CCApi
import be.howest.tuurdelacroix.careconnect.ui.CCAPIUiState
import be.howest.tuurdelacroix.careconnect.ui.CCViewModel
import compose.icons.LineAwesomeIcons
import compose.icons.lineawesomeicons.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MedicationScreen(
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
                modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(MaterialTheme.colors.background))
            {
                PageInfoCard(icon = LineAwesomeIcons.PillsSolid, title = R.string.cc_medication_screen_title)

                Spacer(modifier.width(8.dp))


                val medication = ccAPIUiState.medication
                if (ccAPIUiState.medicationForToday.isNotEmpty())
                {
                    MedicationList(medication, uiState, ccAPIUiState, viewModel)
                }
                else
                {
                    NoContentToShow(R.string.cc_medication_no_content_screen_text, modifier.fillMaxSize())
                }
            }

        }
    }


}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MedicationList(
    medicationList: List<be.howest.tuurdelacroix.careconnect.models.api.Medication>,
    uiState: CCUiState,
    ccAPIUiState: CCAPIUiState.Success,
    viewModel: CCViewModel
)
{
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    ActionButton(
        { showDialog = true },
        R.color.light_green, MaterialTheme.colors.secondary, R.string.cc_medication_take_all_button_text, Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )

    if (showDialog)
    {
        YesNoPopup(
            stringResource(R.string.cc_alert_medication_mark_all_title),
            stringResource(R.string.cc_alert_medication_mark_all_text),
            {
                // TODO
                //UserRepo.markAllMedicationAsTaken(uiState, context)
                viewModel.viewModelScope.launch {
                    try {
                        val updatedMedicationList = CCApi.retrofitService.toggleAllMedicationForTodayAsTaken()
                        viewModel.ccAPIUiState = ccAPIUiState.copy(medication = updatedMedicationList)
                    } catch (ex: Exception)
                    {
                        Log.e("CC", ex.toString())
                    }
                }

                showDialog = false
            },
            {showDialog = false},
            {showDialog = false})
    }

    LazyColumn{
        items(medicationList) {medication ->
            if (medication.scheduleDate == LocalDate.now())
            {
                MedicationView(medication, ccAPIUiState, viewModel)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MedicationView(medication: be.howest.tuurdelacroix.careconnect.models.api.Medication, ccAPIUiState: CCAPIUiState.Success, viewModel: CCViewModel)
{
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    var backgroundColor = MaterialTheme.colors.secondary
    var frontColor = MaterialTheme.colors.primary

    if (medication.scheduleTime!! <= LocalTime.now() && medication.isTaken == 0)
    {
        backgroundColor = MaterialTheme.colors.error
        frontColor = colorResource(R.color.white)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor),
        contentAlignment = Alignment.CenterStart
    )
    {

        Column() {
            Row(modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = medication.type!!,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(2f),
                    color = frontColor
                )
            }

            Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(start = 8.dp, end = 8.dp))

            Row(modifier = Modifier.padding(top = 8.dp)) {
                // Medication Info
                Column(modifier = Modifier.weight(3f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Line before
                        Box(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(4.dp, 42.dp)
                                .background(frontColor)
                        )

                        // Medication name and dose
                        Column(modifier = Modifier.padding(start = 16.dp)) {
                            Text(text = medication.name!!, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = frontColor)
                            Text(text = medication.dose!!, fontSize = 14.sp, color = frontColor)
                        }
                    }

                    // Medication Scheduling
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                    ) {
                        Icon(
                            LineAwesomeIcons.ClockSolid,
                            "Time of medication",
                            Modifier.size(21.dp),
                            tint = frontColor
                        )

                        val formatter = DateTimeFormatter.ofPattern("HH:mm")

                        Text(
                            text = medication.scheduleTime!!.format(formatter),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 8.dp),
                            color = frontColor
                        )
                    }
                }


                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 4.dp)
                        .clickable(
                            onClick = {
                                if (medication.isTaken!! == 0) {
                                    showDialog = true
                                }
                            })
                ) {
                    if (medication.isTaken!! == 0)
                    {
                        NotTakenMedication(frontColor)
                    }
                    else {
                        TakenMedication(frontColor)

                    }
                }
            }

        }
    }

    if (showDialog)
    {
        YesNoPopup(
            stringResource(R.string.cc_alert_medication_mark_single_title),
            stringResource(R.string.cc_alert_medication_mark_single_text, medication.name!!),
            {
                // TODO
               // UserRepo.toggleMedicationState(uiState, medication, context)
                viewModel.viewModelScope.launch {
                    try {
                        val updatedMedicationList = CCApi.retrofitService.toggleMedicationAsTaken(medication.id!!)
                        viewModel.ccAPIUiState = ccAPIUiState.copy(medication = updatedMedicationList)
                        
                    } catch (ex: Exception)
                    {
                        Log.e("CC", ex.toString())
                    }
                }
                showDialog = false
            },
            {showDialog = false},
            {showDialog = false}
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotTakenMedication(frontColor: Color)
{
    Icon(
        LineAwesomeIcons.GlassWhiskeySolid,
        contentDescription = "Innemen",
        tint = frontColor,
        modifier = Modifier.size(32.dp)
    )
    Text(
        text = "Innemen",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = frontColor,
        modifier = Modifier.padding(top = 2.dp)
    )
}

@Composable
fun TakenMedication(frontColor: Color)
{
    Icon(
        LineAwesomeIcons.CheckCircleSolid,
        contentDescription = "Ingenomen",
        tint = frontColor,
        modifier = Modifier.size(32.dp)
    )
    Text(
        text = "Ingenomen",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = frontColor,
        modifier = Modifier.padding(top = 2.dp)
    )
}