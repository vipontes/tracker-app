package br.net.easify.tracker.view.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import br.net.easify.tracker.R
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class HomeFragment : Fragment(), LocationListener {

    private lateinit var mapView: MapView
    private lateinit var mapController: MapController
    private lateinit var locationManager: LocationManager
    private lateinit var currentLocation: Location
    private lateinit var myLocationOverlay: MyLocationNewOverlay


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

        locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 10f, this)

        return view;
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.removeUpdates(this)
    }

    fun addMarker(center: GeoPoint) {
        var marker = Marker(mapView)
        marker.position = center
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView.overlays.clear()
        mapView.overlays.add(marker)

        mapView.invalidate()
    }

    override fun onLocationChanged(location: Location?) {
        location!!.let {
            if (!this::currentLocation.isInitialized) {
                var center: GeoPoint = GeoPoint(it.latitude, it.longitude)
                mapController.animateTo(center)
                addMarker(center)
            }
            currentLocation = it

        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onProviderDisabled(provider: String?) {
    }
}