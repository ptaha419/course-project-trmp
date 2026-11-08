package com.example.ecomonitorapp.data.mappers

import com.example.ecomonitorapp.data.entities.ReportEntity
import com.example.ecomonitorapp.models.Priority
import com.example.ecomonitorapp.models.Report
import com.example.ecomonitorapp.models.ViolationType
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

fun ReportEntity.toDomain(): Report = Report(
    id = id ?: 0L,
    violationType = violationType.let { runCatching { ViolationType.valueOf(it) }.getOrDefault(ViolationType.NOT_SELECTED) },
    description = description,
    latitude = latitude,
    longitude = longitude,
    districtId = districtId,
    photo = photo,
    priority = priority.let { runCatching { Priority.valueOf(it) }.getOrDefault(Priority.NOT_SELECTED) },
    createdAt = Instant.fromEpochMilliseconds(createdAt)
        .toLocalDateTime(TimeZone.currentSystemDefault()),
    reportedBy = reportedBy
)

fun Report.toEntity(): ReportEntity = ReportEntity(
    id = if (id == 0L) null else id,
    violationType = violationType.name,
    description = description,
    latitude = latitude ?: 0.0,
    longitude = longitude ?: 0.0,
    districtId = districtId,
    photo = photo,
    priority = priority.name,
    createdAt = createdAt.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
    reportedBy = reportedBy
)