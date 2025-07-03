package com.example.match_mate.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.match_mate.data.db.dao.UserDao
import com.example.match_mate.data.model.User

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
}