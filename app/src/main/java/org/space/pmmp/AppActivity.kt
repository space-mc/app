package org.space.pmmp

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import org.space.pmmp.screens.MainScreen
import org.space.pmmp.server.LocalServerManager
import org.space.pmmp.server.ServerManager
import org.space.pmmp.server.service.SERVER_NOTIFICATION_CHANNEL_ID
import org.space.pmmp.ui.theme.AppTheme

class AppActivity : ComponentActivity() {

    private val serverManager = ServerManager(this)

    companion object {
        init {
            System.loadLibrary("pmmp")
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        enableEdgeToEdge()

        createNotificationChannel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requestStorageManagerPermission()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        }

        setContent {
            val navController = rememberNavController()

            AppTheme {
                CompositionLocalProvider(LocalServerManager provides serverManager) {
                    CompositionLocalProvider(LocalAppActivity provides this) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            MainScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }

    private fun createNotificationChannel() {
        val name = getString(R.string.server_notification_channel_name)
        val channel = NotificationChannel(
            SERVER_NOTIFICATION_CHANNEL_ID,
            name,
            NotificationManager.IMPORTANCE_DEFAULT
        )

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        manager.createNotificationChannel(channel)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun requestStorageManagerPermission() {
        if (Environment.isExternalStorageManager())
            return

        val intent = Intent(
            ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
            Uri.parse("package:${BuildConfig.APPLICATION_ID}")
        )

        val storagePermissionResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (!Environment.isExternalStorageManager()) {
                Toast.makeText(
                    this,
                    getString(R.string.no_storage_permission),
                    Toast.LENGTH_LONG
                ).show()

                this.finishAffinity()
            }
        }

        storagePermissionResultLauncher.launch(intent)
    }

    private fun translateUriPath(uri: Uri): String {
        // https://gist.github.com/asifmujteba/d89ba9074bc941de1eaa

        return when (uri.authority) {
            "com.android.externalstorage.documents" -> {
                val documentId = DocumentsContract.getDocumentId(uri)
                val (type, path) = documentId.split(':')

                when (type) {
                    "primary" -> Environment.getExternalStorageDirectory().absolutePath + "/$path"
                    else -> ""
                }
            }

            else -> "/"
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestNotificationPermission() {
        val permission = Manifest.permission.POST_NOTIFICATIONS

        val requestNotificationPermission =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                if (!granted) {
                    Toast.makeText(
                        this,
                        getString(R.string.notification_permission_denied),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED)
            return

        requestNotificationPermission.launch(permission)
    }

}

val LocalAppActivity: ProvidableCompositionLocal<AppActivity?> = compositionLocalOf { null }

@Composable
fun composableAppActivity(): AppActivity {
    return LocalAppActivity.current!!
}