package br.net.easify.tracker.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import br.net.easify.tracker.R
import br.net.easify.tracker.database.model.DbActivity
import br.net.easify.tracker.databinding.FragmentHomeBinding
import br.net.easify.tracker.enums.TrackerActivityState
import br.net.easify.tracker.helpers.CustomAlertDialog
import br.net.easify.tracker.viewmodel.HomeViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapController
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var mapController: MapController
    private lateinit var dataBinding: FragmentHomeBinding
    private lateinit var trackerActivity: DbActivity
    private var alertDialog: AlertDialog? = null
    private var currentLocation: GeoPoint? = null

    private val trackerActivityStateObserver = Observer<TrackerActivityState> { state: TrackerActivityState ->
        state.let {
            if (it == TrackerActivityState.idle) {
                dataBinding.startStopButton.text = requireContext().getString(R.string.start)
                dataBinding.startStopButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.colorAccent)
                dataBinding.takePictureButton.visibility = View.GONE
                dataBinding.spinner.visibility = View.GONE
            } else if (it == TrackerActivityState.started) {
                dataBinding.startStopButton.text = requireContext().getString(R.string.stop)
                dataBinding.startStopButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.colorRed)
                dataBinding.takePictureButton.visibility = View.VISIBLE
                dataBinding.spinner.visibility = View.VISIBLE
            }
        }
    }

    private val currentLocationObserver = Observer<GeoPoint> { centerPoint: GeoPoint ->
        centerPoint.let {
            currentLocation = it

            dataBinding.mapView.overlays.clear()
            addMarker(it)
            mapController.animateTo(it)
            if (viewModel.getTrackerState() == TrackerActivityState.idle) {
                viewModel.stopLocationService()
                dataBinding.spinner.visibility = View.GONE
            } else {
                drawPath(viewModel.getCurrentTrackerPath())
            }
            dataBinding.mapView.invalidate()
        }
    }

    private val errorMessageObserver = Observer<String> {
        Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
    }

    private val trackerActivityObserver = Observer<DbActivity> {
        trackerActivity = it
        dataBinding.trackerActivity = trackerActivity
    }

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        viewModel.trackerActivityState.observe(viewLifecycleOwner, trackerActivityStateObserver)
        viewModel.currentLocation.observe(viewLifecycleOwner, currentLocationObserver)
        viewModel.errorMessage.observe(viewLifecycleOwner, errorMessageObserver)
        viewModel.trackerActivity.observe(viewLifecycleOwner, trackerActivityObserver)

        return dataBinding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeMap()
        initializeStartStopButton()
        initializetakePictureButton()

        trackerActivity = DbActivity(
            0,
            0,
            requireContext().getString(R.string.default_duration),
            requireContext().getString(R.string.default_distance),
            requireContext().getString(R.string.default_calories),
            requireContext().getString(R.string.default_rhythm),
            0,
            0,
            "",
            null
        )
        dataBinding.trackerActivity = trackerActivity
    }

    private fun initializeStartStopButton() {
        dataBinding.startStopButton.setOnClickListener(View.OnClickListener {
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
        dataBinding.takePictureButton.setOnClickListener(View.OnClickListener {

        })
    }

    private fun startTrackerActivity() {
        if (viewModel.startLocationService()) {
            viewModel.startTracker()
        }
    }

    private fun stopTrackerActivity() {
        if (viewModel.stopLocationService()) {
            viewModel.stopTracker()
        }
    }

    private fun initializeMap() {
        dataBinding.mapView.setUseDataConnection(true)
        dataBinding.mapView.setTileSource(TileSourceFactory.MAPNIK)
        dataBinding.mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        dataBinding.mapView.setMultiTouchControls(true)

        mapController = dataBinding.mapView.controller as MapController
        mapController.setZoom(16)
    }

    override fun onResume() {
        super.onResume()
        dataBinding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        dataBinding.mapView.onPause()
    }

    fun addMarker(center: GeoPoint) {
        var marker = Marker(dataBinding.mapView)
        marker.position = center
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        dataBinding.mapView.overlays.add(marker)
    }

    private fun drawPath(path: ArrayList<GeoPoint>) {
        var polyline = Polyline(dataBinding.mapView, true, false)
        polyline.setColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
        for (point in path) {
            polyline.addPoint(point)
        }
        dataBinding.mapView.overlays.add(polyline)
    }
}