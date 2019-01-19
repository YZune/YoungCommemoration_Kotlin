package com.suda.yzune.youngcommemoration_kotlin

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.suda.yzune.youngcommemoration_kotlin.bean.EventBean
import com.suda.yzune.youngcommemoration_kotlin.dao.EventDao

@Database(
    entities = [EventBean::class],
    version = 0, exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {
    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java, "young.db"
                        )
                            .build()
                    }
                }
            }
            return INSTANCE!!
        }
    }

    abstract fun eventDao(): EventDao

}