package br.net.easify.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.net.easify.database.dao.*
import br.net.easify.database.model.*

@Database(
    entities = [
        SqliteUser::class,
        SqliteToken::class,
        SqliteRoute::class,
        SqliteRoutePath::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun tokenDao(): TokenDao
    abstract fun routeDao(): RouteDao
    abstract fun routePathDao(): RoutePathDao

    companion object {
        private var instance: AppDatabase? = null
        private var databaseName = "tracker.sqlite"

        fun getAppDataBase(context: Context): AppDatabase {
            if (instance == null) {
                synchronized(AppDatabase::class) {
                    instance = Room.databaseBuilder(
                        context,
                        AppDatabase::class.java,
                        databaseName)
                        .allowMainThreadQueries()
                        .build()
                }
            }
            return instance!!
        }
    }
}