package br.net.easify.tracker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.net.easify.tracker.database.dao.TokenDao
import br.net.easify.tracker.database.dao.UserDao
import br.net.easify.tracker.database.model.TokenLocal
import br.net.easify.tracker.database.model.UserLocal

@Database(
    entities = [
        UserLocal::class,
        TokenLocal::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun tokenDao(): TokenDao

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