package com.example.ecomonitorapp.data.managers

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)

    var currentUserId: Long
        get() = prefs.getLong("current_user_id", -1L)
        set(value) = prefs.edit().putLong("current_user_id", value).apply()

    fun getCurrentUserIdOrNull(): Long? =
        currentUserId.takeIf { it != -1L }
}