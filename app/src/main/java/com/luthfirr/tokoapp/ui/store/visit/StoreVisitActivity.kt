package com.luthfirr.tokoapp.ui.store.visit

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.luthfirr.tokoapp.R
import com.luthfirr.tokoapp.data.local.entity.StoreEntity
import com.luthfirr.tokoapp.data.remote.network.ApiResponse
import com.luthfirr.tokoapp.databinding.ActivityStoreVisitBinding
import com.luthfirr.tokoapp.ui.store.detail.StoreDetailActivity
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

@AndroidEntryPoint
@Suppress("DEPRECATION")
class StoreVisitActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoreVisitBinding
    private val viewModel : StoreVisitViewModel by viewModels()

    private lateinit var currentPhotoPath: String
    private var storeEntity: StoreEntity? = null
    private var visitedSaved = false
    private var photoSaved = false

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
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

        val storeData = intent.getParcelableExtra<StoreEntity>(STORE)

        initView(storeData)
        initObserver()
        initListener(storeData)

    }

    private fun initView(storeData : StoreEntity?) {
        binding.apply {
            storeVisitTvStoreName.text = storeData?.storeName
            storeVisitTvAddress.text = storeData?.address

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

            storeVisitTvLastVisit.text = storeData?.lastVisited.toString()

        }
    }

    private fun initObserver() {
        lifecycleScope.launchWhenResumed {
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

    private fun checkDataIsSaved() {
        if (photoSaved && visitedSaved) {
            val intent = Intent(this@StoreVisitActivity, StoreDetailActivity::class.java)
            intent.putExtra(StoreDetailActivity.STORE, storeEntity?.roomId)
            startActivity(intent)
        }
    }

    private fun initListener(storeData : StoreEntity?) {
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
                    viewModel.updateVisited(it, true, System.currentTimeMillis())
                }
                val intent = Intent(this@StoreVisitActivity, StoreDetailActivity::class.java)
                intent.putExtra(StoreDetailActivity.STORE, storeEntity?.roomId)
                startActivity(intent)
            }

            storeVisitBtnNoVisit.setOnClickListener {
                finish()
            }
        }
    }

//    private val launcherIntentCamera = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) {
//        if (it.resultCode == RESULT_OK) {
//            val imageBitmap = it.data?.extras?.get("data") as Bitmap
//            binding.storeVisitIvBackground.setImageBitmap(imageBitmap)
//        }
//    }

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

    companion object {
        const val STORE = "store"

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}