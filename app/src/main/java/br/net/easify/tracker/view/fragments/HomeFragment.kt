package br.net.easify.tracker.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import br.net.easify.tracker.R
import br.net.easify.tracker.enums.TrackerActivityState
import br.net.easify.tracker.utils.CustomAlertDialog
import br.net.easify.tracker.viewmodel.HomeViewModel
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.fragment_home.*
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var mapView: MapView
    private lateinit var mapController: MapController
    private lateinit var myLocationOverlay: MyLocationNewOverlay
    private lateinit var startStopButton: MaterialButton
    private lateinit var takePictureButton: MaterialButton

    private var alertDialog: AlertDialog? = null

    private val trackerActivityStateObserver = Observer<TrackerActivityState> { state: TrackerActivityState ->
        state.let {
            if (it == TrackerActivityState.idle) {
                startStopButton.text =requireContext().getString(R.string.start)
                startStopButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.colorAccent)
                takePictureButton.visibility = View.GONE
                spinner.visibility = View.GONE
            } else if (it == TrackerActivityState.started) {
                startStopButton.text =requireContext().getString(R.string.stop)
                startStopButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.colorRed)
                takePictureButton.visibility = View.VISIBLE
                spinner.visibility = View.VISIBLE
            }
        }
    }

    private val currentLocationObserver = Observer<GeoPoint> { centerPoint: GeoPoint ->
        centerPoint.let {
            addMarker(it)
            mapController.animateTo(it)
            if ( viewModel.getTrackerState() == TrackerActivityState.idle ) {
                viewModel.stopLocationService()
            }
        }
    }

    private val errorMessageObserver = Observer<String> {
        Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
    }

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        startStopButton = view.findViewById(R.id.startStopButton)
        takePictureButton = view.findViewById(R.id.takePictureButton)
        mapView = view.findViewById(R.id.mapView)

        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        viewModel.trackerActivityState.observe(viewLifecycleOwner, trackerActivityStateObserver)
        viewModel.currentLocation.observe(viewLifecycleOwner, currentLocationObserver)
        viewModel.errorMessage.observe(viewLifecycleOwner, errorMessageObserver)

        initializeMap()
        initializeStartStopButton()
        initializetakePictureButton()

        return view;
    }

    private fun initializeStartStopButton() {
        startStopButton.setOnClickListener(View.OnClickListener {
            viewModel.getTrackerState()?.let {
                if (it == TrackerActivityState.idle) {
                    startTrackerActivity()
                } else {
                    alertDialog = CustomAlertDialog.show(requireContext(), "Tracker Activity",
                        "Do you really want to finish your activity?",
                        "Yes", View.OnClickListener {
                            alertDialog!!.dismiss()
                            stopTrackerActivity()
                        },
                        "No",
                        View.OnClickListener { alertDialog!!.dismiss() }
                    )
                }
            }
        })
    }

    private fun initializetakePictureButton() {
        takePictureButton.setOnClickListener(View.OnClickListener {

        })
    }

    private fun startTrackerActivity() {
        if ( viewModel.startLocationService() ) {
            viewModel.startTracker()
        }
    }

    private fun stopTrackerActivity() {
        if ( viewModel.stopLocationService() ) {
            viewModel.stopTracker()
        }
    }

    private fun initializeMap() {
        mapView.setUseDataConnection(true)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        mapView.setMultiTouchControls(true)

        val gpsMyLocationProvider = GpsMyLocationProvider(context)
        gpsMyLocationProvider.locationUpdateMinDistance = 10f
        gpsMyLocationProvider.locationUpdateMinTime = 5000

        myLocationOverlay = MyLocationNewOverlay(gpsMyLocationProvider, mapView)
        myLocationOverlay.enableMyLocation();

        mapView.overlays.add(myLocationOverlay);

        mapController = mapView.controller as MapController
        mapController.setZoom(16)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        spinner.visibility = View.VISIBLE
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
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