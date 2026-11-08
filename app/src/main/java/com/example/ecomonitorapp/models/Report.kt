package com.example.ecomonitorapp.models

import com.example.ecomonitorapp.data.managers.GeoLoc
import kotlinx.datetime.LocalDateTime

enum class ViolationType { NOT_SELECTED, DUMP_OVERFLOW, UNAUTHORIZED_DUMP,  FIRE, WATER_POLLUTION}
enum class ReportPriority { NOT_SELECTED, HIGH, MEDIUM, LOW}

data class Report (
    val id: Long,
    val violationType: ViolationType = ViolationType.NOT_SELECTED,
    val description: String,
    val geoLoc: GeoLoc,
    val photo: String?,
    val reportPriority: ReportPriority = ReportPriority.NOT_SELECTED,
    val createdAt: LocalDateTime,
    val reportedBy: Long
)