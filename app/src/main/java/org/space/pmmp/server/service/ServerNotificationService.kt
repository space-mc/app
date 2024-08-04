package org.space.pmmp.server.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

const val SERVER_NOTIFICATION_CHANNEL_ID: String = "server_status"
const val SERVER_NOTIFICATION_ID: Int = 0xdead

class ServerNotificationService : Service() {

    private var running: Boolean = false

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent == null) {
            return START_REDELIVER_INTENT
        }


        if (intent.getStringExtra("serverName") == null) {
            return START_REDELIVER_INTENT
        }

        val serverName: String = intent.getStringExtra("serverName")!!

        run(serverName)

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        stop()
    }

    private fun run(serverName: String) {
        if (running)
            return

        val builder = NotificationCompat.Builder(this, SERVER_NOTIFICATION_CHANNEL_ID)

        builder.setContentTitle("Server running")
            .setContentText("The $serverName server is currently running")
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        startForeground(SERVER_NOTIFICATION_ID, builder.build())

        running = true
    }

    private fun stop() {
        if (!running)
            return

        stopForeground(true)
        stopSelf()
    }
}