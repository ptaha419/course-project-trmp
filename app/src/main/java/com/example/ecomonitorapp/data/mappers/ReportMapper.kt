package com.example.ecomonitorapp.data.mappers

import com.example.ecomonitorapp.data.entities.ReportEntity
import com.example.ecomonitorapp.data.helper.GeoLoc
import com.example.ecomonitorapp.models.ReportPriority
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
    geoLoc = GeoLoc(latitude, longitude),
    districtId = districtId,
    photo = photo,
    reportPriority = reportPriority.let { runCatching { ReportPriority.valueOf(it) }.getOrDefault(ReportPriority.NOT_SELECTED) },
    createdAt = Instant.fromEpochMilliseconds(createdAt)
        .toLocalDateTime(TimeZone.currentSystemDefault()),
    reportedBy = reportedBy
)

fun Report.toEntity(): ReportEntity = ReportEntity(
    id = if (id == 0L) null else id,
    violationType = violationType.name,
    description = description,
    latitude = geoLoc.latitude ?: 0.0,
    longitude = geoLoc.longitude ?: 0.0,
    districtId = districtId,
    photo = photo,
    reportPriority = reportPriority.name,
    createdAt = createdAt.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
    reportedBy = reportedBy
)