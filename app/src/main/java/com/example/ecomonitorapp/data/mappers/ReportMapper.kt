package com.example.ecomonitorapp.data.mappers

import com.example.ecomonitorapp.data.entities.ReportEntity
import com.example.ecomonitorapp.data.managers.GeoLoc
import com.example.ecomonitorapp.models.ReportPriority
import com.example.ecomonitorapp.models.Report
import com.example.ecomonitorapp.models.ReportStatus
import com.example.ecomonitorapp.models.ViolationType
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

// функция-расширение: при определённом значении violationType
// выбирается определённый reportPriority
fun ViolationType.toPriority(): ReportPriority = when (this) {
    ViolationType.NOT_SELECTED -> ReportPriority.NOT_SELECTED
    ViolationType.DUMP_OVERFLOW -> ReportPriority.LOW
    ViolationType.UNAUTHORIZED_DUMP -> ReportPriority.MEDIUM
    ViolationType.FIRE -> ReportPriority.HIGH
    ViolationType.WATER_POLLUTION -> ReportPriority.MEDIUM
}

private fun String.toViolationType(): ViolationType =
    runCatching { ViolationType.valueOf(this) }.getOrDefault(ViolationType.NOT_SELECTED)

private fun String.toReportPriority(): ReportPriority =
    runCatching { ReportPriority.valueOf(this) }.getOrDefault(ReportPriority.NOT_SELECTED)

private fun String.toReportStatus(): ReportStatus =
    runCatching { ReportStatus.valueOf(this) }.getOrDefault(ReportStatus.DRAFT)

fun ReportEntity.toDomainOrNull(): Report? {
    val lat = latitude ?: return null
    val lng = longitude ?: return null
    return Report(
        id = id ?: 0L,
        violationType = violationType.toViolationType(),
        description = description,
        geoLoc = GeoLoc(lat, lng),
        photo = photo,
        reportPriority = reportPriority.toReportPriority(),
        createdAt = Instant.fromEpochMilliseconds(createdAt)
            .toLocalDateTime(TimeZone.currentSystemDefault()),
        reportedBy = reportedBy,
        status = status.toReportStatus()
    )
}

fun Report.toEntity(): ReportEntity = ReportEntity(
    id = if (id == 0L) null else id,
    violationType = violationType.name,
    description = description,
    latitude = geoLoc.latitude,
    longitude = geoLoc.longitude,
    photo = photo,
    reportPriority = reportPriority.name,
    createdAt = createdAt.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
    reportedBy = reportedBy,
    status = status.name
)