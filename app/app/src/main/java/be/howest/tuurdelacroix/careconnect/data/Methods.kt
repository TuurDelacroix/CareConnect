package be.howest.tuurdelacroix.careconnect.data

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Process
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import be.howest.tuurdelacroix.careconnect.R
import be.howest.tuurdelacroix.careconnect.ui.CCAPIUiState
import kotlin.system.exitProcess


// Emergency
// Sends an emergency alert message to reachable contacts and the head carer
fun sendEmergencyAlert(uiState: CCAPIUiState.Success, activity: Activity)
{
    // Construct the emergency alert message
    val message = String.format("NOODMELDING! Ik, %s, ben in nood! Kan je mij z.s.m. komen helpen? Ik kon nog net mijn knop indrukken op de APP.", "${uiState.currentUser.firstName} ${uiState.currentUser.lastName}")

    // Get the reachable contacts and head carer phone numbers
    val reachableContacts = uiState.contacts.filter { it.reachable!! }
    val contactPhoneNumbers = reachableContacts.joinToString(separator = "; ") { it.phoneNumber!! }
    val headCarerPhoneNumber = uiState.headCarer.phoneNumber

    // Combine all phone numbers into a single string
    val allPhoneNumbers = "$contactPhoneNumbers; $headCarerPhoneNumber"

    // Toast messages for success and denied alerts
    val successToast = activity.getString(R.string.cc_emergency_message_toast_success)
    val deniedToast = activity.getString(R.string.cc_emergency_message_toast_denied)

    // Send the emergency alert message to all phone numbers
    sendTextMessageToMultiple(activity, message, allPhoneNumbers, successToast, deniedToast)
}

fun callHeadCarer(uiState: CCAPIUiState.Success, activity: Activity)
{
    val phoneNumber = uiState.headCarer.phoneNumber

    // Create an intent to dial the phone number
    val dialIntent = Intent(Intent.ACTION_DIAL)
    dialIntent.data = Uri.parse("tel:$phoneNumber")

    // Start the dialer activity
    activity.startActivity(dialIntent)
}

fun sendAccompanyTextMessage(uiState: CCAPIUiState.Success, activity: Activity)
{
    // Construct the accompanying visit text message
    val message = String.format("Hallo %s! Het is al een tijdje geleden dat ik je zag, kom je binnenkort nog eens langs? Dat lijkt me zeer leuk! Tot binnenkort. Groetjes %s", uiState.headCarer.firstName, "${uiState.currentUser.firstName} ${uiState.currentUser.lastName}")
    val phoneNumber = uiState.headCarer.phoneNumber!!
    val successToast = activity.getString(R.string.cc_accompany_message_toast_success)
    val deniedToast = activity.getString(R.string.cc_accompany_message_toast_success)

    // Send the text message to the head carer
    sendTextMessage(activity, message, phoneNumber, successToast, deniedToast)
}

fun sendUrgentInviteTextMessage(uiState: CCAPIUiState.Success, activity: Activity)
{
    val message = String.format("DRINGEND! Ik heb je dringend nodig, kan je zo snel mogelijk langskomen a.u.b.? Alvast bedankt! Groeten %s", "${uiState.currentUser.firstName} ${uiState.currentUser.lastName}")
    val phoneNumber = uiState.headCarer.phoneNumber!!
    val successToast = activity.getString(R.string.cc_emergency_message_toast_success)
    val deniedToast = activity.getString(R.string.cc_emergency_message_toast_denied)

    sendTextMessage(activity, message, phoneNumber, successToast, deniedToast)
}

private fun sendTextMessage(activity: Activity, message: String, phoneNumber: String, successAlert: String, failAlert: String)
{
    val REQUEST_CODE_SEND_SMS_PERMISSION = 123

    // Create a URI for sending the SMS
    val smsUri = Uri.parse("smsto:$phoneNumber")

    // Check if the SEND_SMS permission is granted
    if (ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        // Create an intent to send the SMS
        val smsIntent = Intent(Intent.ACTION_SENDTO, smsUri)
        smsIntent.putExtra("sms_body", message)

        // Start the SMS activity
        activity.startActivity(smsIntent)
        Toast.makeText(activity, successAlert, Toast.LENGTH_SHORT).show()
    } else {
        // Request the SEND_SMS permission
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.SEND_SMS),
            REQUEST_CODE_SEND_SMS_PERMISSION
        )
        Toast.makeText(activity, failAlert, Toast.LENGTH_SHORT).show()
    }
}

fun sendTextMessageToMultiple(activity: Activity, message: String, phoneNumbers: String, successAlert: String, failAlert: String)
{
    val REQUEST_CODE_SEND_SMS_PERMISSION = 123

    // Create a URI for sending the SMS to multiple recipients
    val smsUri = Uri.parse("smsto:$phoneNumbers")

    // Check if the SEND_SMS permission is granted
    if (ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        // Create an intent to send the SMS to multiple recipients
        val smsIntent = Intent(Intent.ACTION_SENDTO, smsUri)
        smsIntent.putExtra("sms_body", message)

        // Start the SMS activity
        activity.startActivity(smsIntent)
        Toast.makeText(activity, successAlert, Toast.LENGTH_SHORT).show()
    } else {
        // Request the SEND_SMS permission
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.SEND_SMS),
            REQUEST_CODE_SEND_SMS_PERMISSION
        )
        Toast.makeText(activity, failAlert, Toast.LENGTH_SHORT).show()
    }
}

// Alerts
fun showToastAlert(context: Context, text: String)
{
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}

// Close application when error
fun exitApplication(context: Context) {
    // Create an intent to go to the home screen
    val intent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_HOME)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

    // Start the home screen activity
    context.startActivity(intent)
    // Kill the current process and exit the application
    Process.killProcess(Process.myPid())
    exitProcess(0)
}