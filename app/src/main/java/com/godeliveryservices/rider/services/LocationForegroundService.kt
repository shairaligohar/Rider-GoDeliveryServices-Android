package com.godeliveryservices.rider.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.godeliveryservices.rider.R
import com.godeliveryservices.rider.network.ApiService
import com.godeliveryservices.rider.repository.PreferenceRepository
import com.google.android.gms.location.*
import io.reactivex.schedulers.Schedulers


class LocationForegroundService : Service() {

    private val locationProvider: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(
            this
        )
    }

    companion object {
        const val CHANNEL_ID = "Foreground_Service_Channel"
    }

    fun startService(context: Context) {
        val intent = Intent(context, LocationForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        startForeground(1, getNotification())
        subscribeToLocationUpdates()
        return START_STICKY
    }

    private fun subscribeToLocationUpdates() {

        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 60 * 1000

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult == null)
                    return
                for (location in locationResult.locations) {
                    location?.let {
                        ApiService.create().updateLocation(
                            PreferenceRepository(this@LocationForegroundService).getRiderId(),
                            "${location.latitude},${location.longitude}"
                        )
                            .observeOn(Schedulers.io())
                            .subscribeOn(Schedulers.io())
                            .subscribe(
                                { success -> success },
                                { error -> error })
                    }
                }
            }
        }

        locationProvider.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager?.createNotificationChannel(serviceChannel)
        }
    }


    private fun getNotification(): Notification {
        return with(NotificationCompat.Builder(this, CHANNEL_ID)) {
            priority = NotificationCompat.PRIORITY_HIGH
            setContentTitle("Location Service")
            setContentText("Sending location updates to server ...")
            setOngoing(true)
            setSmallIcon(R.drawable.logo_without_text)
            setOnlyAlertOnce(true)
            return@with build()
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}