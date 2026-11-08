package com.example.ecomonitorapp.models

import java.time.LocalDateTime

enum class ViolationType { NOT_SELECTED, DUMP_OVERFLOW, UNAUTHORIZED_DUMP,  FIRE, WATER_POLLUTION}
enum class Priority { NOT_SELECTED, HIGH, MEDIUM, LOW}

data class Report (
    val id: Long,
    val violationType: ViolationType = ViolationType.NOT_SELECTED,
    val description: String,
    val longitude: Double,
    val latitude: Double,
    val districtId: Long,
    val photo: String?,
    val priority: Priority = Priority.NOT_SELECTED,
    val createdAt: LocalDateTime,
    val reportedBy: Long
)