package br.net.easify.tracker.database.dao

import androidx.room.*
import br.net.easify.tracker.database.model.DbRoute
import br.net.easify.tracker.database.model.DbRoutePath

@Dao
interface RoutePathDao {

    @Query("SELECT * FROM user_route_path WHERE user_route_path_id = :userRoutePathId")
    fun getRoutePath(userRoutePathId: Int): DbRoutePath?

    @Query("SELECT * FROM user_route_path WHERE user_route_id = :userRouteId ORDER BY user_route_path_id ASC")
    fun getPathFromRoute(userRouteId: Int): List<DbRoutePath>

    @Insert(onConflict = OnConflictStrategy.FAIL)
    fun insert(dbRoutePath: DbRoutePath)

    @Query("DELETE FROM user_route_path WHERE user_route_path_id = :userRoutePathId")
    fun delete(userRoutePathId: Int)

    @Update
    fun update(dbRoutePath: DbRoutePath)
}