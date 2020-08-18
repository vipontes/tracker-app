package br.net.easify.tracker.repositories.database.dao

import androidx.room.*
import br.net.easify.tracker.repositories.database.model.SqliteRoute

@Dao
interface RouteDao {

    @Query("SELECT * FROM user_route WHERE user_route_id = :userRouteId")
    fun getRoute(userRouteId: Long): SqliteRoute?

    @Query("SELECT * FROM user_route ORDER BY user_route_id DESC")
    fun getAll(): List<SqliteRoute>?

    @Insert(onConflict = OnConflictStrategy.FAIL)
    fun insert(sqliteRoute: SqliteRoute): Long

    @Query("DELETE FROM user_route WHERE user_route_id = :userRouteId")
    fun delete(userRouteId: Long)

    @Query("DELETE FROM user_route")
    fun delete()

    @Update
    fun update(sqliteRoute: SqliteRoute): Int
}