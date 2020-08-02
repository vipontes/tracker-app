package br.net.easify.tracker.database.dao

import androidx.room.*
import br.net.easify.tracker.database.model.DbActivity

@Dao
interface ActivityDao {

    @Query("SELECT * FROM activity WHERE activity_id = :activityId")
    fun getActivity(activityId: Long): DbActivity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(activity: DbActivity): Long

    @Query("DELETE FROM activity")
    fun deleteAll()

    @Update
    fun update(activity: DbActivity)
}