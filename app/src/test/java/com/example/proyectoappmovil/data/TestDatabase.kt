package com.example.proyectoappmovil.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.core.app.ApplicationProvider
import com.example.proyectoappmovil.data.local.AppDatabase

class TestDatabase {

    companion object {
        @Suppress("UNUSED_PARAMETER")
        fun create(context: Context = ApplicationProvider.getApplicationContext()): AppDatabase =
            Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
    }
}