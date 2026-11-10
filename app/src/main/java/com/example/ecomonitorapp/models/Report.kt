package com.example.ecomonitorapp.models

import com.example.ecomonitorapp.data.managers.GeoLoc
import kotlinx.datetime.LocalDateTime

enum class ViolationType(val displayName: String) {
    DUMP_OVERFLOW("Заполненные мусорные баки"),
    UNAUTHORIZED_DUMP("Несанкционированная свалка"),
    FIRE("Очаг пожара"),
    WATER_POLLUTION("Загрязнение водного объекта"),
    NOT_SELECTED("Не выбрано") }

enum class ReportPriority(val displayName: String) {
    HIGH("Высокий приоритет"),
    MEDIUM("Средний приоритет"),
    LOW("Низкий приоритет"),
    NOT_SELECTED("Не выбран")
}

enum class ReportStatus(val displayName: String) {
    DRAFT("Черновик"),
    SENT("Отправлено"),
    REJECTED("Отклонено"),
    ACCEPTED("Принято к рассмотрению")
}

data class Report (
    val id: Long,
    val violationType: ViolationType = ViolationType.NOT_SELECTED,
    val description: String,
    val geoLoc: GeoLoc,
    val photo: String?,
    val reportPriority: ReportPriority = ReportPriority.NOT_SELECTED,
    val createdAt: LocalDateTime,
    val reportedBy: Long,
    val status: ReportStatus = ReportStatus.SENT,
    val violationCard: Long
)