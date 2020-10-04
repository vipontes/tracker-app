package br.net.easify.tracker.view.route

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import br.net.easify.database.model.SqliteRoute
import br.net.easify.database.model.SqliteRoutePath
import br.net.easify.tracker.R
import br.net.easify.tracker.databinding.ActivityRouteBinding
import br.net.easify.tracker.helpers.MapHelper
import br.net.easify.tracker.viewmodel.RouteViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapController
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

class RouteActivity : AppCompatActivity() {

    private lateinit var viewModel: RouteViewModel
    private lateinit var dataBinding: ActivityRouteBinding
    private lateinit var mapController: MapController

    private var routeId: Long = 0

    private val routeObserver = Observer<SqliteRoute> {

    }

    private val routePathObserver = Observer<List<SqliteRoutePath>> { path: List<SqliteRoutePath> ->
        if (path.isNotEmpty()) {
            val geoPointPath = MapHelper().geoPointFromRoutePath(path)
            drawPath(geoPointPath)
            addMarker(geoPointPath[0])
            mapController.animateTo(geoPointPath[0])
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.routeId = intent.getLongExtra("routeId", 0)

        dataBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_route)
        initializeMap()
        initializeButtons()

        viewModel = ViewModelProviders.of(this).get(RouteViewModel::class.java)
        viewModel.route.observe(this, routeObserver)
        viewModel.routePath.observe(this, routePathObserver)
        viewModel.getRoute(this.routeId)
        viewModel.getRoutePath(this.routeId)
    }

    private fun initializeButtons() {
        dataBinding.backButton.setOnClickListener(View.OnClickListener {
            finish()
        })
    }

    private fun initializeMap() {
        dataBinding.mapView.setUseDataConnection(true)
        dataBinding.mapView.setTileSource(TileSourceFactory.MAPNIK)
        dataBinding.mapView.zoomController.setVisibility(
            CustomZoomButtonsController.Visibility.NEVER
        );
        dataBinding.mapView.setMultiTouchControls(true)

        mapController = dataBinding.mapView.controller as MapController
        mapController.setZoom(16)
    }

    private fun addMarker(center: GeoPoint) {
        val marker = Marker(dataBinding.mapView)
        marker.position = center
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        dataBinding.mapView.overlays.add(marker)
    }

    private fun drawPath(path: ArrayList<GeoPoint>) {
        val polyline = Polyline(dataBinding.mapView, true, false)
        polyline.setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
        for (point in path) {
            polyline.addPoint(point)
        }
        dataBinding.mapView.overlays.add(polyline)
    }
}