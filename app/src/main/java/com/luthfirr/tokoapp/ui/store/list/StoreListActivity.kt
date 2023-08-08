package com.luthfirr.tokoapp.ui.store.list

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.luthfirr.tokoapp.R
import com.luthfirr.tokoapp.data.local.entity.StoreEntity
import com.luthfirr.tokoapp.data.remote.network.ApiResponse
import com.luthfirr.tokoapp.databinding.ActivityListStoreBinding
import com.luthfirr.tokoapp.ui.store.visit.StoreVisitActivity
import com.luthfirr.tokoapp.utils.makeToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class StoreListActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityListStoreBinding
    private val viewModel: StoreListViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var listStoreAdapter = StoreListAdapter()
    private var storeList = mutableListOf<StoreEntity>()

    private var boundsBuilder = LatLngBounds.builder()

    var myLat = 0.0
    var myLong = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.list_store_fragment_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        initView()
        initObserver()
        initListener()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        getMyLastLocation()
        viewModel.fetchStoreList()

    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                else -> {
                    finish()
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION))
        {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                myLat = location?.latitude ?: 0.0
                myLong = location?.longitude ?: 0.0
                if (location != null) {
                    showStartMarker(location)
                } else {
                    makeToast(this@StoreListActivity, "Location is not found. Try again")
                }
            }
        }
        else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun showStartMarker(location: Location) {
        val startLocation = LatLng(location.latitude, location.longitude)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startLocation, 17f))
        mMap.addCircle(
            CircleOptions()
                .center(startLocation)
                .radius(100.0)
                .fillColor(R.color.white)
                .strokeWidth(0f)
        )
    }

    private fun initObserver() {
        lifecycleScope.launchWhenStarted {
            viewModel.storeListResult.collectLatest {

                binding.listStoreProgressBar.isVisible = it is ApiResponse.Loading

                if (it is ApiResponse.Success) {
                    storeList.clear()
                    storeList.addAll(it.data)
                    listStoreAdapter.submitList(storeList.toList())
                    for (i in storeList) {
                        if (i.latitude != null && i.longitude != null) {
                            val storeLocation =
                                LatLng(i.latitude.toDouble(), i.longitude.toDouble())
                            boundsBuilder.include(storeLocation)
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(storeLocation)
                                    .title(i.storeName)
                                    .snippet(i.address)
                            )
                        }
                    }
                    val bounds = boundsBuilder.build()
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 128))
                }
            }
        }
    }

    private fun initListener() {

        binding.listStoreBtnBack.setOnClickListener {
            finish()
        }

        listStoreAdapter.storeListener = {
            intent = Intent(this@StoreListActivity, StoreVisitActivity::class.java)
            intent.putExtra(StoreVisitActivity.STORE, it)
            intent.putExtra(StoreVisitActivity.MY_LAT, myLat)
            intent.putExtra(StoreVisitActivity.MY_LONG, myLong)
            startActivity(intent)
        }
    }

    private fun initView() {
        binding.listStoreRvStore.adapter = listStoreAdapter
    }

}

