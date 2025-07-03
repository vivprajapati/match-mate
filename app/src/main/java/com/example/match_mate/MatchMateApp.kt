package com.example.match_mate

import android.app.Application
import com.example.match_mate.data.db.DatabaseManager

class MatchMateApp : Application() {
    override fun onCreate() {
        super.onCreate()
        DatabaseManager.initDatabase(this)

    }

}