package com.example.ecomonitorapp.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// возможные роли пользователя
enum class UserRole {APPLICANT, MODERATOR, INSPECTOR}

@Entity(tableName = "user")
data class User (
    @PrimaryKey(autoGenerate = true)
    val id: Int?,

    @ColumnInfo(name = "first_name")
    val firstName: String,
    @ColumnInfo(name = "middle_name")
    val middleName: String?,
    @ColumnInfo(name = "last_name")
    val lastName: String,

    @ColumnInfo(name = "phone_number")
    val phoneNumber: String,
    @ColumnInfo(name = "passport")
    val passport: String,

    @ColumnInfo(name = "email")
    val email: String,
    @ColumnInfo(name = "password")
    val password: String,
    @ColumnInfo(name = "role")
    val role: UserRole = UserRole.APPLICANT // по умолчанию
    )