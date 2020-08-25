package br.net.easify.tracker.repositories.database.dao

import androidx.room.*
import br.net.easify.tracker.repositories.database.model.SqliteRoutePath

@Dao
interface RoutePathDao {

    @Query("SELECT * FROM user_route_path WHERE user_route_path_id = :userRoutePathId")
    fun getRoutePath(userRoutePathId: Long): SqliteRoutePath?

    @Query("SELECT * FROM user_route_path WHERE user_route_id = :userRouteId ORDER BY user_route_path_id ASC")
    fun getPathFromRoute(userRouteId: Long): List<SqliteRoutePath>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(sqliteRoutePath: SqliteRoutePath): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(collection: List<SqliteRoutePath>)


    @Query("DELETE FROM user_route_path WHERE user_route_path_id = :userRoutePathId")
    fun delete(userRoutePathId: Long)

    @Query("DELETE FROM user_route_path")
    fun delete()

    @Update
    fun update(sqliteRoutePath: SqliteRoutePath)
}