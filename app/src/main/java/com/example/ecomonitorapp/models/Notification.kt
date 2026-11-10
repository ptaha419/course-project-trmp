package com.example.ecomonitorapp.models

import kotlinx.datetime.LocalDateTime

enum class NotificationStatus(val displayName: String) {
    NOT_SENT("Не отправлено"),
    SENT("Отправлено"),
    NOT_READ("Не прочитано"),
    READ("Прочитано")
}

data class Notification (
    val id: Long,
    val report: Long,
    val message: String,
    val status: NotificationStatus = NotificationStatus.NOT_SENT,
    val createdAt: LocalDateTime,
    val sendBy: Long,
    val sendTo: Long
)