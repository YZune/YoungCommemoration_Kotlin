package com.suda.yzune.youngcommemoration

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.migration.Migration
import android.content.Context
import com.suda.yzune.youngcommemoration.bean.EventBean
import com.suda.yzune.youngcommemoration.bean.SingleAppWidgetBean
import com.suda.yzune.youngcommemoration.dao.EventDao
import com.suda.yzune.youngcommemoration.dao.SingleWidgetDao

@Database(
    entities = [EventBean::class, SingleAppWidgetBean::class],
    version = 3, exportSchema = false
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
                            .addMigrations(migration1to2)
                            .addMigrations(migration2to3)
                            .build()
                    }
                }
            }
            return INSTANCE!!
        }

        private val migration1to2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE EventBean RENAME TO EventBean_old;")
                database.execSQL("CREATE TABLE EventBean(id INTEGER NOT NULL, content TEXT NOT NULL, year INTEGER NOT NULL, month INTEGER NOT NULL, day INTEGER NOT NULL, type INTEGER NOT NULL, path TEXT NOT NULL, isFav INTEGER NOT NULL, msg TEXT NOT NULL, sortNum INTEGER NOT NULL DEFAULT 0, PRIMARY KEY (id));")
                database.execSQL("INSERT INTO EventBean (id, content, year, month, day, type, path, isFav, msg, sortNum) SELECT id, content, year, month, day, type, path, isFav, msg, 0 FROM EventBean_old;")
                database.execSQL("DROP TABLE EventBean_old;")
                database.execSQL("ALTER TABLE SingleAppWidgetBean RENAME TO SingleAppWidgetBean_old;")
                database.execSQL("CREATE TABLE SingleAppWidgetBean(id INTEGER NOT NULL, eventId INTEGER NOT NULL, withPic INTEGER NOT NULL, weight INTEGER NOT NULL, bgColor INTEGER NOT NULL, textColor INTEGER NOT NULL, PRIMARY KEY (id), FOREIGN KEY (eventId) REFERENCES EventBean (id) ON DELETE CASCADE ON UPDATE CASCADE);")
                database.execSQL("INSERT INTO SingleAppWidgetBean (id, eventId, withPic, weight, bgColor, textColor) SELECT id, eventId, withPic, 1, bgColor, textColor FROM SingleAppWidgetBean_old;")
                database.execSQL("DROP TABLE SingleAppWidgetBean_old;")
            }
        }

        private val migration2to3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE SingleAppWidgetBean RENAME TO SingleAppWidgetBean_old;")
                database.execSQL("CREATE TABLE SingleAppWidgetBean(id INTEGER NOT NULL, eventId INTEGER NOT NULL, withPic INTEGER NOT NULL, weight INTEGER NOT NULL, bgColor INTEGER NOT NULL, textColor INTEGER NOT NULL, textHorizontal INTEGER NOT NULL, contentSize INTEGER NOT NULL, daySize INTEGER NOT NULL, msgSize INTEGER NOT NULL, PRIMARY KEY (id), FOREIGN KEY (eventId) REFERENCES EventBean (id) ON DELETE CASCADE ON UPDATE CASCADE);")
                database.execSQL("INSERT INTO SingleAppWidgetBean (id, eventId, withPic, weight, bgColor, textColor, textHorizontal, contentSize, daySize, msgSize) SELECT id, eventId, withPic, 1, bgColor, textColor, 0, 14, 20, 14 FROM SingleAppWidgetBean_old;")
                database.execSQL("DROP TABLE SingleAppWidgetBean_old;")
            }
        }
    }

    abstract fun eventDao(): EventDao
    abstract fun singleWidgetDao(): SingleWidgetDao
}