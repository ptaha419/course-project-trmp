package com.example.ecomonitorapp.data.db

import android.content.Context
import androidx.room.Room

// отложенная инициализация БД, синглтон
// не надо создавать новую БД в каждом запросе
object DbProvider {
    // кэшированное поле для хранения уже созданного экземпляра AppDatabase
    private var _db: AppDatabase? = null

    // двойная проверка с блокировкой, чтобы избежать повторного создания БД
    fun getDatabase(context: Context): AppDatabase {
        return _db ?: synchronized(this) {
            _db ?: buildDatabase(context).also { _db = it }
        }
    }

    // создание БД через Room
    private fun buildDatabase(context: Context): AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "ecomonitor_db"
    )
        .build()
}