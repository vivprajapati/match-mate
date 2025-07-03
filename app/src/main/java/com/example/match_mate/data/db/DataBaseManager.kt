package com.example.match_mate.data.db

import android.content.Context
import androidx.room.Room

object DatabaseManager{

    private const val DATABASE_NAME = "mach_mate_database"
    @Volatile
    private var INSTANCE: AppDatabase? = null

    // Get the database instance
    fun initDatabase(context: Context){
        synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            ).build()
            INSTANCE = instance
            instance
        }
    }

    fun getDatabase(): AppDatabase {
        return INSTANCE ?: throw IllegalStateException("Database not initialized")
    }


}