package br.net.easify.tracker.helpers

import br.net.easify.tracker.repositories.database.model.SqliteRoutePath
import org.osmdroid.util.GeoPoint

class MapHelper {

    fun geoPointFromRoutePath(routePath: List<SqliteRoutePath>)
            : ArrayList<GeoPoint> {
        val path: ArrayList<GeoPoint> = arrayListOf()
        for (point in routePath) {
            path.add(
                GeoPoint(
                    point.user_route_path_lat,
                    point.user_route_path_lng
                )
            )
        }

        return path
    }
    
}