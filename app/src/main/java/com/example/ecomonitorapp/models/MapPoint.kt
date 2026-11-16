package com.example.ecomonitorapp.models

data class MapPoint(
    val reportId: Long,
    val latitude: Double,
    val longitude: Double,
    val violationType: ViolationType,
    val cardId: Long?,
    val cardStatus: ViolationStatus?,
)