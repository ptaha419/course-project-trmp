package com.example.ecomonitorapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.ecomonitorapp.models.ReportPriority
import com.example.ecomonitorapp.models.ViolationType

@Entity(tableName = "report",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["reported_by"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("reported_by")])
data class ReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "violation_type") val violationType: String = ViolationType.NOT_SELECTED.name,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double,
    @ColumnInfo(name = "photo") val photo: String?,
    @ColumnInfo(name = "report_priority") val reportPriority: String = ReportPriority.NOT_SELECTED.name,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "reported_by") val reportedBy: Long
)