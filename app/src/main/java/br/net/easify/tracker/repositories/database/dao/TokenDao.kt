package br.net.easify.tracker.repositories.database.dao

import androidx.room.*
import br.net.easify.tracker.repositories.database.model.SqliteToken

@Dao
interface  TokenDao {

    @Query("SELECT * FROM token LIMIT 1")
    fun get(): SqliteToken?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(sqliteToken: SqliteToken)

    @Query("DELETE FROM token")
    fun delete()
}