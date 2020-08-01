package br.net.easify.tracker.database.dao

import androidx.room.*
import br.net.easify.tracker.database.model.DbRoute

@Dao
interface RouteDao {

    @Query("SELECT * FROM user_route WHERE user_route_id = :userRouteId")
    fun getRoute(userRouteId: Int?): DbRoute?

    @Query("SELECT * FROM user_route ORDER BY user_route_id DESC")
    fun getAll(): List<DbRoute>?

    @Insert(onConflict = OnConflictStrategy.FAIL)
    fun insert(dbRoute: DbRoute)

    @Query("DELETE FROM user_route WHERE user_route_id = :userRouteId")
    fun delete(userRouteId: Int)

    @Update
    fun update(dbRoute: DbRoute)
}