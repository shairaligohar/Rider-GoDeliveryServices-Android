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
    }

    fun saveRiderData(shop: Rider) {
        sharedPreference.edit().apply {
            putString(Keys.RIDER_USER_NAME, shop.Username)
            putString(Keys.RIDER_NAME, shop.Name)
            putLong(Keys.RIDER_ID, shop.RiderID)
        }.apply()
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
}