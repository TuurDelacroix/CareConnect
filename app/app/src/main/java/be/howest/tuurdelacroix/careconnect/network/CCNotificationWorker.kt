package be.howest.tuurdelacroix.careconnect.network

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import be.howest.tuurdelacroix.careconnect.MainActivity
import be.howest.tuurdelacroix.careconnect.R
import be.howest.tuurdelacroix.careconnect.models.api.Medication
import java.time.LocalTime
import java.time.temporal.ChronoUnit

// Worker class responsible for scheduling medication notifications
class CCNotificationWorker(
    private val context: Context,
    private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams)
{
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {

        // Retrieve medication list for today from the API
        val medicationList = CCApi.retrofitService.getMedicationForToday()
        val currentTime = LocalTime.now()

        medicationList.forEach { medication ->
            // Check if medication is not taken and its schedule time is in the future
            if (medication.isTaken == 0 && medication.scheduleTime!!.isAfter(currentTime)) {
                scheduleNotifications(medication)
            }
        }

        return Result.success()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun scheduleNotifications(medication: Medication) {
        val notificationManager = NotificationManagerCompat.from(context)

        // Calculate time until medication intake
        val timeUntilMedication = ChronoUnit.MINUTES.between(LocalTime.now(), medication.scheduleTime)

        // Create the notification builder
        val notificationBuilder = createNotification(context, medication)

        if (timeUntilMedication in 16..30)
        {

            // Request permission to post notifications if not granted
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    context as MainActivity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
                return
            }

            val uniqueId = "${medication.id}_${System.currentTimeMillis()}"
            // Schedule notification 30 minutes before medication intake
            notificationManager.notify(
                uniqueId.hashCode(),
                notificationBuilder
                    .setContentText("Over $timeUntilMedication min: ${medication.name}")
                    .build()
            )

        }
        else if (timeUntilMedication in 1..15)
        {
            // Schedule notification for the specified time until medication intake
            notificationManager.notify(
                medication.id!!.toInt(),
                notificationBuilder
                    .setContentText("Over $timeUntilMedication min: ${medication.name}")
                    .build()
            )
        }
        else if (timeUntilMedication in 0..1)
        {
            // Schedule immediate notification for medication intake
            notificationManager.notify(
                medication.id!!.toInt(),
                notificationBuilder.build()
            )
            Log.d("NOTIFICATION", "Between 0 & 1 = now ")
        }
    }

    private fun createNotification(context: Context, medication: Medication): NotificationCompat.Builder {
        return NotificationCompat.Builder(context,"due_medication_channel")
            .setSmallIcon(R.drawable.cc_app_logo)
            .setContentTitle("CareConnect - Medicatie")
            .setContentText("${medication.name} nu innemen!")
            .setContentIntent(createPendingIntent())
    }

    // Create the pending intent for opening the app when notification is clicked
    private fun createPendingIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

}