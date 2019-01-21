package com.suda.yzune.youngcommemoration

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.suda.yzune.youngcommemoration.bean.EventBean
import com.suda.yzune.youngcommemoration.bean.SingleAppWidgetBean
import com.suda.yzune.youngcommemoration.dao.EventDao
import com.suda.yzune.youngcommemoration.dao.SingleWidgetDao

@Database(
    entities = [EventBean::class, SingleAppWidgetBean::class],
    version = 1, exportSchema = false
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
    abstract fun singleWidgetDao(): SingleWidgetDao
}