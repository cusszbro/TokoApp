package com.luthfirr.tokoapp.ui.store.visit

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.luthfirr.tokoapp.R
import com.luthfirr.tokoapp.data.local.entity.StoreEntity
import com.luthfirr.tokoapp.data.remote.network.ApiResponse
import com.luthfirr.tokoapp.databinding.ActivityStoreVisitBinding
import com.luthfirr.tokoapp.ui.store.detail.StoreDetailActivity
import com.luthfirr.tokoapp.utils.GeofenceBroadcastReceiver
import com.luthfirr.tokoapp.utils.createCustomTempFile
import com.luthfirr.tokoapp.utils.makeToast
import com.luthfirr.tokoapp.utils.rotateFile
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar

@AndroidEntryPoint
@Suppress("DEPRECATION")
class StoreVisitActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoreVisitBinding
    private val viewModel : StoreVisitViewModel by viewModels()

    private var storeData: StoreEntity? = null
    private lateinit var currentPhotoPath: String
    private var visitedSaved = false
    private var photoSaved = false

    val calendar = Calendar.getInstance()
    val currentTimeInMillis = calendar.timeInMillis

    private lateinit var geofencingClient: GeofencingClient
    private lateinit var geofence: Geofence
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationStatus = Geofence.GEOFENCE_TRANSITION_EXIT

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this@StoreVisitActivity, GeofenceBroadcastReceiver::class.java)
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        intent.action = ACTION_GEOFENCE_EVENT
        @SuppressLint("UnspecifiedImmutableFlag")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(
                this@StoreVisitActivity,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getBroadcast(
                this@StoreVisitActivity,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val transition = intent.getIntExtra(TRANSITION, -1)
            if (transition != -1) {
                transitionListener(transition)
            }
        }
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
                permissions[Manifest.permission.CAMERA] ?: false -> {
                    binding.storeVisitBtnCamera.performClick()
                }
                else -> {
                    finish()
                }
            }
        }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreVisitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        geofencingClient = LocationServices.getGeofencingClient(this@StoreVisitActivity)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@StoreVisitActivity)

        storeData = intent.getParcelableExtra<StoreEntity>(STORE)
        val myLat = intent.getDoubleExtra(MY_LAT, 0.0)
        val myLong = intent.getDoubleExtra(MY_LONG, 0.0)

        initView()
        initObserver()
        initListener()

    }

    override fun onResume() {
        super.onResume()
        if (storeData != null) {
            addGeofence()
        }
        registerReceiver(receiver, IntentFilter(ACTION_GEOFENCE_TRANSITION))
    }

    override fun onPause() {
        super.onPause()
        removeGeofence()
        unregisterReceiver(receiver)
    }

    private fun initView() {
        binding.apply {
            storeVisitTvStoreName.text = storeData?.storeName
            storeVisitTvAddress.text = storeData?.address

//            storeVisitTvStoreName.text = lat.toString()
//            storeVisitTvAddress.text = long.toString()

            storeVisitTvOutletType.text = resources.getString(
                R.string.tipe_outlet,
                storeData?.storeCode
            )

            storeVisitTvDisplayType.text = resources.getString(
                R.string.tipe_display,
                storeData?.channelName
            )

            storeVisitTvDisplaySubtype.text = resources.getString(
                R.string.sub_tipe_display,
                storeData?.subchannelName
            )

            storeVisitTvErtm.text = resources.getString(
                R.string.ertm,
                storeData?.areaName
            )

            storeVisitTvPareto.text = resources.getString(
                R.string.pareto,
                storeData?.areaId
            )

            storeVisitTvEmerchandising.text = resources.getString(
                R.string.e_merchandising,
                storeData?.dcName
            )

            storeVisitTvLastVisit.text = storeData?.lastVisited.toString() ?: currentTimeInMillis.toString()

            if (storeData?.picture != null) {
                val myFile = File(storeData!!.picture)

                myFile.let { file ->
                    rotateFile(file)
                    storeVisitIvBackground.setImageBitmap(BitmapFactory.decodeFile(file.path))
                }
            }

        }
    }

    private fun initObserver() {
        lifecycleScope.launchWhenStarted {
            viewModel.storePictureResult.collectLatest { apiResponse ->
                binding.storeVisitProgressBar.isVisible = apiResponse is ApiResponse.Loading
                if (apiResponse is ApiResponse.Error) makeToast(this@StoreVisitActivity, apiResponse.errorMessage)
                if (apiResponse is ApiResponse.Success) {
                    photoSaved = true
                }
            }

            viewModel.storeVisitedResult.collectLatest { apiResponse ->
                binding.storeVisitProgressBar.isVisible = apiResponse is ApiResponse.Loading
                if (apiResponse is ApiResponse.Error) makeToast(this@StoreVisitActivity, apiResponse.errorMessage)
                if (apiResponse is ApiResponse.Success) {
                    val date = SimpleDateFormat.getDateInstance().format(apiResponse.data)
                    binding.storeVisitTvLastVisit.text = date.toString()
                    visitedSaved = true
                }
            }
        }
    }

    private fun initListener() {

        binding.apply {
            storeVisitBtnReset.setOnClickListener {
                storeVisitProgressBar.isVisible = true
                CoroutineScope(Dispatchers.Main).launch {
                    onRestart()
                    delay(2000)
                    storeVisitProgressBar.isVisible = false
                }
            }

            storeVisitBtnNavigation.setOnClickListener {
                makeToast(this@StoreVisitActivity, "Kamu memilih Menu Tombol Navigasi")
            }

            storeVisitBtnCamera.setOnClickListener {
                startTakePhoto()
            }

            storeVisitBtnGps.setOnClickListener {
                makeToast(this@StoreVisitActivity, "Kamu memilih Menu Tombol GPS")
            }

            storeVisitBtnVisit.setOnClickListener {
                storeData?.roomId?.let {
                    viewModel.updatePicture(it, currentPhotoPath)
                    viewModel.updateVisited(it, true, currentTimeInMillis)
                }
                val intent = Intent(this@StoreVisitActivity, StoreDetailActivity::class.java)
                intent.putExtra(StoreDetailActivity.STORE, storeData)
                startActivity(intent)
            }

            storeVisitBtnNoVisit.setOnClickListener {
                finish()
            }
        }
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@StoreVisitActivity,
                "com.luthfirr.tokoapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)

            myFile.let { file ->
                rotateFile(file)
                binding.storeVisitIvBackground.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private fun transitionListener(geofenceTransition: Int) {
        when (geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                binding.apply {
                    storeVisitLocationStatus.text = "Lokasi sesuai"
                }
                locationStatus = Geofence.GEOFENCE_TRANSITION_ENTER
                checkVisitButton()
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                binding.apply {
                    storeVisitLocationStatus.text = "Lokasi belum sesuai"

                }
                locationStatus = Geofence.GEOFENCE_TRANSITION_EXIT
                checkVisitButton()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION))
        {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location == null) {
                    makeToast(this@StoreVisitActivity, "Location is not found. Try again")

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

//    @SuppressLint("MissingPermission")
//    private fun getMyLastLocation() {
//        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION))
//        {
//            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
//                if (location != null) {
//                    makeToast(this@StoreVisitActivity, "Location found")
//                } else {
//                    val locationRequest =
//                        LocationRequest.Builder(
//                            Priority.PRIORITY_HIGH_ACCURACY,
//                            TimeUnit.SECONDS.toMillis(1)
//                        )
//                            .build()
//                    val locationCallback = object : LocationCallback() {
//                        override fun onLocationResult(location: LocationResult) {
//                            getMyLastLocation()
//                        }
//                    }
//                    fusedLocationClient.requestLocationUpdates(
//                        locationRequest,
//                        locationCallback,
//                        null
//                    )
//                    makeToast(this@StoreVisitActivity, "Location is not found. Try again")
//                }
//            }
//        }
//        else {
//            requestPermissionLauncher.launch(
//                arrayOf(
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//                )
//            )
//        }
//    }

    private fun getGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofence(geofence)
        }.build()
    }

    @SuppressLint("MissingPermission")
    private fun addGeofence() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        ) {
            removeGeofence()
            geofencingClient.addGeofences(getGeofencingRequest(), geofencePendingIntent)
                .addOnSuccessListener { Log.d("test", "geofence added") }
                .addOnFailureListener {
                    it.printStackTrace()
                    makeToast(this@StoreVisitActivity, it.localizedMessage!!)
                }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                )
            } else {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
            }
        }
    }

    private fun removeGeofence() {
        geofencingClient.removeGeofences(geofencePendingIntent)
    }

    private fun checkVisitButton() {
        binding.storeVisitBtnVisit.isEnabled = locationStatus == Geofence.GEOFENCE_TRANSITION_ENTER && currentPhotoPath != null
    }

    companion object {
        const val STORE = "store"
        const val MY_LAT = "my_latitude"
        const val MY_LONG = "my_longitude"

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10

        const val ACTION_GEOFENCE_TRANSITION = "action_transition"
        const val TRANSITION = "transition"
        const val ACTION_GEOFENCE_EVENT = "geofence"
    }
}