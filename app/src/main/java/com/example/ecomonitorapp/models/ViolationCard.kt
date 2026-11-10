package com.example.ecomonitorapp.models

import kotlinx.datetime.LocalDateTime

enum class ViolationStatus(val displayName: String) {
    UNDER_CONSIDERATION("На рассмотрении"),
    SENT_TO_INSPECTOR("Отправлено инспектору"),
    LIQUIDATION_PROCESS("В процессе ликвидации"),
    LIQUIDATED("Ликвидировано")
}

data class ViolationCard (
    val id: Long,
    val report: Long,
    val status: String,
    val createdAt: LocalDateTime,
    val verifiedBy: Long
)