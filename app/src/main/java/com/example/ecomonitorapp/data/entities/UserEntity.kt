package com.example.ecomonitorapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import com.example.ecomonitorapp.models.UserRole

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "first_name") val firstName: String,
    @ColumnInfo(name = "middle_name") val middleName: String?,
    @ColumnInfo(name = "last_name") val lastName: String,
    @ColumnInfo(name = "phone_number") val phoneNumber: String,
    @ColumnInfo(name = "passport") val passport: String,
    @ColumnInfo(name = "password") val passwordHash: String?, // храним хеш
    @ColumnInfo(name = "role") val role: String = UserRole.APPLICANT.name
)