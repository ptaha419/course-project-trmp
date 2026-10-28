package com.example.ecomonitorapp.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// возможные роли пользователя
enum class UserRole {APPLICANT, MODERATOR, INSPECTOR}

data class User(
    val id: Int?,
    val email: String,
    val firstName: String,
    val middleName: String?,
    val lastName: String,
    val phoneNumber: String,
    val passport: String,
    val role: UserRole = UserRole.APPLICANT
)