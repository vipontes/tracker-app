package br.net.easify.tracker.database.dao

import androidx.room.*
import br.net.easify.tracker.database.model.TokenLocal
import br.net.easify.tracker.database.model.UserLocal

@Dao
interface  TokenDao {

    @Query("SELECT * FROM token LIMIT 1")
    fun get(): TokenLocal?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(token: TokenLocal?)

    @Query("DELETE FROM token")
    fun delete()
}