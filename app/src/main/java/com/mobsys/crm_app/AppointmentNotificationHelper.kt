package com.mobsys.crm_app

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.mobsys.crm_app.model.Appointment
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object AppointmentNotificationHelper {

    private const val CHANNEL_ID = "appointment_today_channel"
    private const val NOTIFICATION_ID = 1001

    fun showTodayNotification(context: Context, appointments: List<Appointment>) {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) // "2026-02-24"

        val next = appointments
            .filter { it.start.startsWith(today) }
            .minByOrNull { it.start }
            ?: return

        createChannel(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return

        val text = "\"${next.title}\" – ${next.ort}"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Heutige Termine")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Heutige Termine",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Benachrichtigung über Termine für heute" }
            context.getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }
}


