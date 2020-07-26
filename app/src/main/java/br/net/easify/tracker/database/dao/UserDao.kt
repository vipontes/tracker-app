package br.net.easify.tracker.database.dao

import androidx.room.*
import br.net.easify.tracker.database.model.UserLocal

@Dao
interface UserDao {

    @Query("SELECT * FROM user WHERE user_id = :userId")
    fun getUser(userId: Int?): UserLocal?

    @Query("SELECT * FROM user LIMIT 1")
    fun getLoggedUser(): UserLocal?

    @Query("SELECT * FROM user ORDER BY user_id")
    fun getAll(): List<UserLocal>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: UserLocal?)

    @Query("DELETE FROM user WHERE user_id = :userId")
    fun delete(userId: Int?)

    @Query("DELETE FROM user")
    fun delete()

    @Update
    fun update(user: UserLocal?)
}