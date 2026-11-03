package com.example.ecomonitorapp.data.db

import android.content.Context
import androidx.room.Room

// отложенная инициализация БД, синглтон
// не надо создавать новую БД в каждом запросе
object DbProvider {
    @Volatile
    private var _db: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return _db ?: synchronized(this) {
            _db ?: buildDatabase(context).also { _db = it }
        }
    }

    private fun buildDatabase(context: Context): AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "ecomonitor_db"
    )
        .fallbackToDestructiveMigration() // при изменении схемы БД, она просто пересоздаётся
        .build()
}