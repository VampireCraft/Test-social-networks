package ru.bikbulatov.comeWithMe.createEvent.ui

import android.Manifest
import android.app.Activity
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.here.android.mpa.common.ApplicationContext
import com.here.android.mpa.common.GeoCoordinate
import com.here.android.mpa.common.MapEngine
import com.here.android.mpa.common.MapSettings
import com.here.android.mpa.common.OnEngineInitListener
import com.here.android.mpa.search.*
import kotlinx.android.synthetic.main.fragment_create_position.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import ru.bikbulatov.comeWithMe.BaseApp
import ru.bikbulatov.comeWithMe.BuildConfig
import ru.bikbulatov.comeWithMe.R
import ru.bikbulatov.comeWithMe.core.geoLocation.GeoLocationListener
import ru.bikbulatov.comeWithMe.core.model.Status
import ru.bikbulatov.comeWithMe.core.ui.BaseFragment
import ru.bikbulatov.comeWithMe.createEvent.ui.vm.CreateEventViewModel
import ru.bikbulatov.comeWithMe.databinding.FragmentCreatePositionBinding
import java.io.File
import java.util.*


class FragmentCreateEventPosition : BaseFragment() {
    private lateinit var binding: FragmentCreatePositionBinding
    private lateinit var viewModel: CreateEventViewModel

    private var positionMarker: Marker? = null
    private lateinit var addressPoint: GeoPoint
    val listToCompleteGeoPositions: MutableList<GeoPoint> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreatePositionBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(CreateEventViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //initMapEngine()
        getLocation()
        configureBackBtn()
        configureNextBtn()
        paintProgressBar()
        requestPermission()
        observeOnEventCreate()
        hideKeyboard()
        configureAddressChangeListener()
    }

    private fun hideKeyboard() {
        (requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(
                this.requireView().applicationWindowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
    }

    private fun configureNextBtn() {
        binding.btnNext.setOnClickListener {
            if (isAddressValid()) {
                binding.btnNext.isEnabled = false
                viewModel.createEvent()
            }
        }
    }

    private fun getLocation() {
        (requireActivity() as? GeoLocationListener)?.getLocation { task ->
            var location: Location? = null
            if (task?.result != null)
                location = task.result
            if (location == null) {
                (requireActivity() as? GeoLocationListener)?.getNewLocation()
            } else {
                configureMap(location.latitude, location.longitude)
                addMarker(location.latitude, location.longitude)
            }
        }
    }

    private fun configureMap(latitude: Double, longitude: Double) {
        binding.map.setTileSource(TileSourceFactory.MAPNIK)
        configureOsm()

        //touch on map
        val mReceive: MapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(clickedPosition: GeoPoint): Boolean {
                addMarker(clickedPosition.latitude, clickedPosition.longitude)
                return false
            }

            override fun longPressHelper(p: GeoPoint): Boolean {
                return false
            }
        }
        binding.map.overlays.add(MapEventsOverlay(mReceive))
        binding.map.controller.setZoom(16)
        binding.map.controller.setCenter(GeoPoint(latitude, longitude))
        centerMapOnMyLocation()
    }

    private fun configureOsm() {
        Configuration.getInstance().apply {
            userAgentValue = BuildConfig.APPLICATION_ID
            load(BaseApp.instance, BaseApp.sharedPreferences)
            osmdroidTileCache =
                File(BaseApp.instance.cacheDir.toString() + "/osmCache/")
            osmdroidBasePath =
                File(BaseApp.instance.cacheDir.toString() + "/osmBase/")
            tileFileSystemCacheMaxBytes = 50000000
        }
    }

    private fun paintProgressBar() {
        binding.pbEventCreate.indeterminateDrawable.colorFilter =
            PorterDuffColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.bright_turquoise
                ), PorterDuff.Mode.SRC_IN
            )
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            binding.pbEventCreate.setProgress(95, true)
        } else
            binding.pbEventCreate.progress = 95
    }

    private fun configureBackBtn() {
        binding.ivBtnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            1000
        )
    }

    fun initMapEngine(){
        MapSettings.setDiskCacheRootPath(File(activity?.getExternalFilesDir(null),".here-map-data").absolutePath)
        MapEngine.getInstance().init(ApplicationContext(requireActivity()), OnEngineInitListener { error: OnEngineInitListener.Error? ->
            if (error != OnEngineInitListener.Error.NONE)
                Log.e("HERE Error", error.toString())
            else {
                Log.d("HERE", "Successful init engine")
            }
        })
    }

    fun addRotationGesturesToMap(mapView: MapView) {
        val rotationGestureOverlay = RotationGestureOverlay(mapView)
        rotationGestureOverlay.isEnabled = true
        mapView.setMultiTouchControls(true)
        mapView.overlays.add(rotationGestureOverlay)
    }

    fun addMarker(latitude: Double, longitude: Double) {
        if (positionMarker == null) {
            positionMarker = Marker(binding.map)
        }
        addressPoint = GeoPoint(latitude, longitude)
        getAddressBasedOnLocation(latitude, longitude)
        positionMarker?.position = addressPoint
        positionMarker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        binding.map.overlays.add(positionMarker)
    }

    private fun centerMapOnMyLocation() {
        val myLocationOverlay =
            MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), binding.map)
        myLocationOverlay.disableMyLocation()
        myLocationOverlay.disableFollowLocation()
        myLocationOverlay.isDrawAccuracyEnabled = true
        myLocationOverlay.runOnFirstFix {
            centerMapOn(myLocationOverlay.myLocation)
        }
        binding.map.overlays.add(myLocationOverlay)
    }

    private fun centerMapOn(geoPoint: GeoPoint) {
        val mapController = binding.map.controller
        lifecycleScope.launch(Dispatchers.Main) {
            mapController.setCenter(geoPoint)
            mapController.animateTo(geoPoint)
        }
    }

    private fun getAddressBasedOnLocation(latitude: Double, longitude: Double) {
        try {

            val addressRequest = ReverseGeocodeRequest(GeoCoordinate(latitude,longitude))
            addressRequest.execute { location, errorCode ->
                if (errorCode == ErrorCode.NONE)
                    binding.tilAddress.editText?.setText(location?.address?.city+" "+location?.address?.street+" "+ location?.address?.houseNumber)
                else
                    Log.e("HERE Error Request", errorCode.toString())
            }

            viewModel.eventCreationRequest.address = binding.tilAddress.editText?.text.toString()
            viewModel.eventCreationRequest.coordinateX = latitude
            viewModel.eventCreationRequest.coordinateY = longitude
        } catch (e: Exception) {
            Log.e("Geocoder", e.localizedMessage)
        }
    }

    private fun isAddressValid(): Boolean {
        return !binding.tilAddress.editText?.text.isNullOrEmpty()
    }

    private fun observeOnEventCreate() {
        viewModel.createEventResponse.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> Log.d("createEventResponse", "LOADING")
                Status.SUCCESS -> {
                    Toast.makeText(requireContext(), "Событие успешно создано", Toast.LENGTH_SHORT)
                        .show()
                    binding.btnNext.isEnabled = true
                    viewModel.navigator?.openStartScreen()
                }
                Status.ERROR -> {
                    Log.d("createEventResponse", "ERROR")
                    it.error?.let { it1 -> showError(it1) }
                    binding.btnNext.isEnabled = true
                }
            }
        })
    }

    private fun configureAddressChangeListener() {
        binding.tieAddress.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                lifecycleScope.launch(Dispatchers.IO) {
                    listToCompleteGeoPositions.clear()
                    if (s.toString().isNotEmpty()) {
                        val geoCoder = Geocoder(requireContext())
                        val addresses: List<Address> =
                            geoCoder.getFromLocationName(s.toString(), 1);
                        val listToComplete: MutableList<String> = mutableListOf()
                        for (address in addresses) {
                            listToComplete.add(
                                "${address.locality ?: ""}, " + "${address.thoroughfare ?: ""}, " + (address.subThoroughfare
                                    ?: "")
                            )
                            listToCompleteGeoPositions.add(
                                GeoPoint(
                                    address.latitude,
                                    address.longitude
                                )
                            )
                        }
                        withContext(Dispatchers.Main) {
                            binding.tieAddress.setAdapter(
                                ArrayAdapter(
                                    requireContext(),
                                    android.R.layout.simple_dropdown_item_1line,
                                    listToComplete
                                )
                            )
                        }
                    }
                }
            }
        })

        binding.tieAddress.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                if (listToCompleteGeoPositions.size > position) {
                    centerMapOn(listToCompleteGeoPositions[position])
                    addMarker(
                        listToCompleteGeoPositions[position].latitude,
                        listToCompleteGeoPositions[position].longitude
                    )
                }
            }
    }

    override fun showLoading() {
        TODO("Not yet implemented")
    }

    override fun hideLoading() {
        TODO("Not yet implemented")
    }

    override fun showError(error: String) {
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT)
            .show()
    }
}