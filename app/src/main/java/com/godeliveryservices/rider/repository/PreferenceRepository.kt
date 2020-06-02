package com.godeliveryservices.rider.repository

import android.content.Context
import androidx.preference.PreferenceManager
import com.godeliveryservices.rider.model.Rider

class PreferenceRepository(val context: Context) {

    private val sharedPreference by lazy { PreferenceManager.getDefaultSharedPreferences(context) }

    object Keys {
        const val RIDER_USER_NAME = "RIDER_USER_NAME"
        const val RIDER_NAME = "RIDER_NAME"
        const val RIDER_ID = "RIDER_ID"
        const val STATUS = "STATUS"
        const val Logged_IN = "LoggedIn"
    }

    fun saveRiderData(rider: Rider) {
//    fun saveRiderData() {
        sharedPreference.edit().apply {
            putString(Keys.RIDER_USER_NAME, rider.Username)
            putString(Keys.RIDER_NAME, rider.Name)
            putLong(Keys.RIDER_ID, rider.RiderID)
//            putString(Keys.RIDER_USER_NAME, "Dummy")
//            putString(Keys.RIDER_NAME, "Dummy")
//            putLong(Keys.RIDER_ID, 9)
            putString(Keys.STATUS, "Active")
            putBoolean(Keys.Logged_IN, true)
        }.apply()
    }

    fun updateRiderStatus(status: String) {
        sharedPreference.edit().putString(Keys.STATUS, status).apply()
    }

    fun getRiderId(): Long {
        return sharedPreference.getLong(Keys.RIDER_ID, 0)
    }

    fun getRiderName(): String? {
        return sharedPreference.getString(Keys.RIDER_NAME, "")
    }

    fun getRiderUserName(): String? {
        return sharedPreference.getString(Keys.RIDER_USER_NAME, "")
    }

    fun getRiderStatus(): String? {
        return sharedPreference.getString(Keys.STATUS, "")
    }
}