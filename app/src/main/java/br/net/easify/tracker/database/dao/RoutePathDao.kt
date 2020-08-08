package br.net.easify.tracker.database.dao

import androidx.room.*
import br.net.easify.tracker.database.model.DbRoutePath

@Dao
interface RoutePathDao {

    @Query("SELECT * FROM user_route_path WHERE user_route_path_id = :userRoutePathId")
    fun getRoutePath(userRoutePathId: Long): DbRoutePath?

    @Query("SELECT * FROM user_route_path WHERE user_route_id = :userRouteId ORDER BY user_route_path_id ASC")
    fun getPathFromRoute(userRouteId: Long): List<DbRoutePath>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(dbRoutePath: DbRoutePath): Long

    @Query("DELETE FROM user_route_path WHERE user_route_path_id = :userRoutePathId")
    fun delete(userRoutePathId: Long)

    @Update
    fun update(dbRoutePath: DbRoutePath)
}