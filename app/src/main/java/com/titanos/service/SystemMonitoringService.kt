package com.titanos.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.titanos.R
import com.titanos.TitanApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SystemMonitoringService : Service() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        createChannel()
        startForeground(101, ongoingNotification("TitanOS monitoring active"))

        val repository = (application as TitanApplication).container.systemRepository
        scope.launch {
            repository.watchMetrics().collectLatest { metrics ->
                if (metrics.temperatureC > 43f || (metrics.batteryPercent < 10 && metrics.cpuPercent > 75)) {
                    val manager = getSystemService(NotificationManager::class.java)
                    manager.notify(202, ongoingNotification("System alert: thermal/drain risk"))
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    override fun onBind(intent: Intent?): IBinder? = null

    private fun ongoingNotification(content: String): Notification {
        return NotificationCompat.Builder(this, "titan_monitoring")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("TitanOS")
            .setContentText(content)
            .setOngoing(true)
            .build()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "titan_monitoring",
                "Titan Monitoring",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }
}
