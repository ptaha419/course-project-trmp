package com.example.ecomonitorapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.ecomonitorapp.models.ViolationStatus

@Entity(tableName = "violation_card",
    foreignKeys = [
        ForeignKey(
            entity = ReportEntity::class,
            parentColumns = ["id"],
            childColumns = ["report"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["verified_by"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("report"),
        Index("verified_by")
    ])
data class ViolationCardEntity (
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "report") val report: Long,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "status") val status: String = ViolationStatus.UNDER_CONSIDERATION.name,
    @ColumnInfo(name = "verified_by") val verifiedBy: Long,
)