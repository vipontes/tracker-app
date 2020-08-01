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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.net.easify.tracker.R
import br.net.easify.tracker.background.services.LocationService
import br.net.easify.tracker.enums.TrackerActivityState
import br.net.easify.tracker.model.ErrorResponse
import br.net.easify.tracker.utils.Constants
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
    private var currentLocation = GeoPoint(0, 0)
    private lateinit var startStopButton: MaterialButton
    private lateinit var takePictureButton: MaterialButton

    private var alertDialog: AlertDialog? = null

    private val onLocationServiceNotification: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val resultCode = intent.getIntExtra(Constants.resultCode, Activity.RESULT_CANCELED)
            if (resultCode == Activity.RESULT_OK) {
                val latitude = intent.getDoubleExtra(Constants.latitude, 0.0)
                val longitude = intent.getDoubleExtra(Constants.longitude, 0.0)

                currentLocation = GeoPoint(latitude, longitude)
                addMarker(currentLocation)
                mapController.animateTo(currentLocation)

                val state = viewModel.getTrackerActivityState()
                state?.let {
                    if (it == TrackerActivityState.idle) {
                        viewModel.stopLocationService()
                    } else if (it == TrackerActivityState.started) {
                        // Save path
                    }
                }

                spinner.visibility = View.GONE
            }
        }
    }

    private val trackerActivityStateObserver = Observer<TrackerActivityState> { state: TrackerActivityState ->
        state.let {
            if (it == TrackerActivityState.idle) {
                startStopButton.text =requireContext().getString(R.string.start)
                startStopButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.colorAccent)
                takePictureButton.visibility = View.GONE
            } else if (it == TrackerActivityState.started) {
                startStopButton.text =requireContext().getString(R.string.stop)
                startStopButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.colorRed)
                takePictureButton.visibility = View.VISIBLE
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        startStopButton = view.findViewById(R.id.startStopButton)
        takePictureButton = view.findViewById(R.id.takePictureButton)

        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)

        viewModel.trackerActivityState.observe(viewLifecycleOwner, trackerActivityStateObserver)

        mapView = view.findViewById(R.id.mapView)

        initializeMap()
        initializeStartStopButton()
        initializetakePictureButton()

        return view;
    }

    private fun initializeStartStopButton() {
        startStopButton.setOnClickListener(View.OnClickListener {
            viewModel.isActivityStarted.value?.let {
                val isActivityStarted = it
                if (!isActivityStarted) {
                    startTrackerActivity()
                    viewModel.isActivityStarted.value = true
                } else {
                    alertDialog = CustomAlertDialog.show(requireContext(), "Tracker Activity",
                        "Do you really want to finish your activity?",
                        "Yes", View.OnClickListener {
                            alertDialog!!.dismiss()
                            stopTrackerActivity()
                            viewModel.isActivityStarted.value = false
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

        viewModel.startLocationService(true)
    }

    private fun stopTrackerActivity() {

        viewModel.stopLocationService()
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

        val intentFilter = IntentFilter(LocationService.locationChangeAction)
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(onLocationServiceNotification, intentFilter)

        viewModel.startLocationService()

        spinner.visibility = View.VISIBLE
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