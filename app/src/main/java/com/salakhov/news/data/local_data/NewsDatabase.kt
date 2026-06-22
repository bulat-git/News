package com.salakhov.news.data.local_data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [SubscriptionDbModel::class, ArticleDbModel::class],
    version = 1,
    exportSchema = false
)
abstract class NewsDatabase: RoomDatabase() {

    abstract fun newsDao(): NewsDao

    companion object {

        private var instance: NewsDatabase? = null

        private val LOCK = Any()

        fun getInstance(context: Context): NewsDatabase {

            instance?.let { return it }
            synchronized(LOCK) {
                instance?.let { return it }

                return Room.databaseBuilder(
                    context = context,
                    klass = NewsDatabase::class.java,
                    name = "news.db"
                ).fallbackToDestructiveMigration(dropAllTables = true).build().also { instance = it }
            }
        }
    }
}