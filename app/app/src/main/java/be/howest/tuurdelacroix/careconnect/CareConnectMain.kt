package be.howest.tuurdelacroix.careconnect

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import androidx.work.*
import be.howest.tuurdelacroix.careconnect.localroom.CCDatabase
import be.howest.tuurdelacroix.careconnect.network.CCNotificationWorker
import be.howest.tuurdelacroix.careconnect.ui.screens.StartScreen
import be.howest.tuurdelacroix.careconnect.ui.screens.outputDir
import be.howest.tuurdelacroix.careconnect.ui.theme.CareConnectTheme
import java.io.File
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class MainActivity : ComponentActivity() {

    // Camera
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("Camera", "Permission granted")
        } else {
            Log.d("Camera", "Permission denied")
        }
    }

    // Request the camera permission
    private fun requestCamPermission()
    {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("Camera", "Permission previously granted")
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            ) -> Log.d("Camera", "Show camera permission request")

            else -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    private fun getOutputDirectory(): File {
        Log.d("Camera", "getting output dir")

        // Get the external media directories available on the device
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            // Create a subdirectory in the first available external media directory
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }

        // Return the media directory if it exists, otherwise return the internal files directory
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create a notification channel for medication reminders (for Android Oreo and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val notificationChannel = NotificationChannel(
                "due_medication_channel",
                "Take medication",
                NotificationManager.IMPORTANCE_HIGH
            )

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        // Schedule periodic work for medication notifications using WorkManager
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)  // Only when connected to unmetered network, optional
            .setRequiresDeviceIdle(false)
            .build()

        val notificationWorkRequest = PeriodicWorkRequestBuilder<CCNotificationWorker>(
            1, TimeUnit.MINUTES) // Every minute
            .setConstraints(constraints)
            .addTag("cc_noti")
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueueUniquePeriodicWork(
                "medicationNotification",
                ExistingPeriodicWorkPolicy.REPLACE,
                notificationWorkRequest
            )

        // Create the Room database instance
        val database = Room.databaseBuilder(
            applicationContext,
            CCDatabase::class.java, "cc-database"
        ).fallbackToDestructiveMigration().build()

        // Request camera permission and get the output directory
        requestCamPermission()
        outputDir = getOutputDirectory()

        // Nav Hide
        window.decorView.apply {
            systemUiVisibility =
                (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
        setContent {
            CareConnectTheme {

                val navController = rememberNavController()
                StartScreen(database, navController = navController)
            }
        }
    }

}

// Not applying systemuivisibility changes
//@RequiresApi(Build.VERSION_CODES.O)
//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview()
//{
//    CareConnectTheme {
//        StartScreen()
//    }
//}