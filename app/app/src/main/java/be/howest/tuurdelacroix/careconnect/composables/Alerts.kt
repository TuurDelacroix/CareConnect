package be.howest.tuurdelacroix.careconnect.composables

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import be.howest.tuurdelacroix.careconnect.R

@Composable
fun YesNoPopup(
    title: String,
    text: String,
    onYesClick: () -> Unit,
    onNoClick: () -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = title.uppercase(), fontWeight = FontWeight.Bold) },
        text = { Text(text = text) },
        confirmButton = {
            Button(
                onClick = onYesClick,
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(R.color.light_green), contentColor = MaterialTheme.colors.secondary)
            ) {
                Text(text = stringResource(R.string.cc_alert_confirmation_button_text))
            }
        },
        dismissButton = {
            Button(
                onClick = onNoClick,
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error, contentColor = MaterialTheme.colors.secondary)
            ) {
                Text(text = stringResource(R.string.cc_alert_dismiss_button_text))
            }
        }
    )
}

@Composable
fun YesPopup(
    title: String,
    text: String,
    onYesClick: () -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = title) },
        text = { Text(text = text) },
        confirmButton = {
            Button(
                onClick = onYesClick
            ) {
                Text(text = stringResource(R.string.cc_alert_ok_button_text))
            }
        }
    )
}