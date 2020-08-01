package br.net.easify.tracker.view.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.net.easify.tracker.R
import br.net.easify.tracker.background.services.LocationService
import br.net.easify.tracker.utils.Constants
import br.net.easify.tracker.utils.ServiceHelper
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class HomeFragment : Fragment() {

    private lateinit var mapView: MapView
    private lateinit var mapController: MapController
    private lateinit var myLocationOverlay: MyLocationNewOverlay
    private var currentLocation = GeoPoint(0,0)

    private val onLocationServiceNotification: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val resultCode = intent.getIntExtra(Constants.resultCode, Activity.RESULT_CANCELED)
            if ( resultCode == Activity.RESULT_OK) {
//                val provider = intent.getStringExtra(Constants.provider)
//                val altitude = intent.getDoubleExtra(Constants.altitude, 0.0)
                val latitude = intent.getDoubleExtra(Constants.latitude, 0.0)
                val longitude = intent.getDoubleExtra(Constants.longitude, 0.0)

                currentLocation = GeoPoint(latitude, longitude)
                addMarker(currentLocation)
                mapController.animateTo(currentLocation)

                val gpsService = LocationService()
                val gpsIntent = Intent(requireContext(), gpsService::class.java)
                if (ServiceHelper(requireContext().applicationContext).isMyServiceRunning(gpsService::class.java)) {
                    requireContext().stopService(gpsIntent)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        mapView = view.findViewById(R.id.mapView)
        mapView.setUseDataConnection(true)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        mapView.setMultiTouchControls(true)

        val gpsMyLocationProvider = GpsMyLocationProvider(context)
        gpsMyLocationProvider.locationUpdateMinDistance = 10f
        gpsMyLocationProvider.locationUpdateMinTime = 5000

        myLocationOverlay = MyLocationNewOverlay(gpsMyLocationProvider, mapView)
        myLocationOverlay.enableMyLocation();

//        val posIcon: Bitmap =
//            BitmapFactory.decodeResource(requireContext().resources, R.drawable.ic_about)
//        myLocationOverlay.setPersonIcon(posIcon)

        mapView.overlays.add(myLocationOverlay);

        mapController = mapView.controller as MapController
        mapController.setZoom(16)

        return view;
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()

        val intentFilter = IntentFilter(LocationService.locationChangeAction)
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(onLocationServiceNotification, intentFilter)

        val gpsService = LocationService()
        val gpsIntent = Intent(requireContext(), gpsService::class.java)
        if (!ServiceHelper(requireContext().applicationContext).isMyServiceRunning(gpsService::class.java)) {
            requireContext().startService(gpsIntent)
        }
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(onLocationServiceNotification);
    }

    fun addMarker(center: GeoPoint) {

        var marker = Marker(mapView)
        marker.position = center
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView.overlays.clear()
        mapView.overlays.add(marker)

        mapView.invalidate()
    }
}