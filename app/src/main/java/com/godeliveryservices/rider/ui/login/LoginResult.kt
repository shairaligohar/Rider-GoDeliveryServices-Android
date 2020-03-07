package com.godeliveryservices.rider.ui.login

import com.godeliveryservices.rider.model.Rider

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
    val success: Boolean = false,
    val code: Int = 0,
    val rider: Rider? = null
)

