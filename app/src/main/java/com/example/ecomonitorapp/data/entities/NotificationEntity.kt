package com.example.ecomonitorapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.ecomonitorapp.models.NotificationStatus

@Entity(tableName = "notification",
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
            childColumns = ["sent_to", "sent_by"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("report"),
        Index("sent_to"),
        Index("sent_by")
    ])
data class NotificationEntity (
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "report") val report: Long,
    @ColumnInfo(name = "message") val message: String,
    @ColumnInfo(name = "status") val status: String = NotificationStatus.NOT_SENT.name,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "send_by") val sendBy: Long,
    @ColumnInfo(name = "send_to") val sendTo: Long,
)