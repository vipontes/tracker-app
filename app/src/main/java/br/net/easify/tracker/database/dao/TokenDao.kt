package br.net.easify.tracker.database.dao

import androidx.room.*
import br.net.easify.tracker.database.model.DbToken

@Dao
interface  TokenDao {

    @Query("SELECT * FROM token LIMIT 1")
    fun get(): DbToken?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(dbToken: DbToken)

    @Query("DELETE FROM token")
    fun delete()
}