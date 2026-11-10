package com.example.ecomonitorapp.data.mappers

import com.example.ecomonitorapp.data.entities.ViolationCardEntity
import com.example.ecomonitorapp.models.ViolationCard
import com.example.ecomonitorapp.models.ViolationStatus
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

private fun String.toViolationStatus(): ViolationStatus =
    runCatching { ViolationStatus.valueOf(this) }.getOrDefault(ViolationStatus.UNDER_CONSIDERATION)

fun ViolationCardEntity.toDomain() = ViolationCard(
    id = id ?: 0L,
    report = report,
    status = status.toViolationStatus(),
    createdAt = Instant.fromEpochMilliseconds(createdAt)
        .toLocalDateTime(TimeZone.currentSystemDefault()),
    verifiedBy = verifiedBy
)