package br.net.easify.tracker.view.route

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import br.net.easify.tracker.R
import br.net.easify.tracker.databinding.ActivityRouteBinding
import br.net.easify.tracker.viewmodel.RouteViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapController

class RouteActivity : AppCompatActivity() {

    private lateinit var viewModel: RouteViewModel
    private lateinit var dataBinding: ActivityRouteBinding
    private lateinit var mapController: MapController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_route)
        initializeMap()
        initializeButtons()

        viewModel = ViewModelProviders.of(this).get(RouteViewModel::class.java)
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
}