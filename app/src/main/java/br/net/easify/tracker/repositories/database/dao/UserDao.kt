package br.net.easify.tracker.repositories.database.dao

import androidx.room.*
import br.net.easify.tracker.repositories.database.model.SqliteUser

@Dao
interface UserDao {

    @Query("SELECT * FROM user LIMIT 1")
    fun getLoggedUser(): SqliteUser?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(sqliteUser: SqliteUser): Long

    @Query("DELETE FROM user WHERE user_id = :userId")
    fun delete(userId: Long)

    @Query("DELETE FROM user")
    fun delete()

    @Update
    fun update(sqliteUser: SqliteUser?)
}