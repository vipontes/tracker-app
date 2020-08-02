package br.net.easify.tracker.database.dao

import androidx.room.*
import br.net.easify.tracker.database.model.DbUser

@Dao
interface UserDao {

    @Query("SELECT * FROM user WHERE user_id = :userId")
    fun getUser(userId: Long?): DbUser?

    @Query("SELECT * FROM user LIMIT 1")
    fun getLoggedUser(): DbUser?

    @Query("SELECT * FROM user ORDER BY user_id")
    fun getAll(): List<DbUser>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(dbUser: DbUser): Long

    @Query("DELETE FROM user WHERE user_id = :userId")
    fun delete(userId: Long)

    @Query("DELETE FROM user")
    fun delete()

    @Update
    fun update(dbUser: DbUser?)
}