package com.luthfirr.tokoapp.ui.store.visit

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.luthfirr.tokoapp.R
import com.luthfirr.tokoapp.data.local.entity.StoreEntity
import com.luthfirr.tokoapp.databinding.ActivityStoreVisitBinding
import com.luthfirr.tokoapp.utils.createCustomTempFile
import com.luthfirr.tokoapp.utils.rotateFile
import java.io.File

@Suppress("DEPRECATION")
class StoreVisitActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoreVisitBinding
    private lateinit var currentPhotoPath: String

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
        initListener()

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

            storeVisitTvLastVisit.text = if (storeData?.lastVisited.toString() != null) storeData?.lastVisited.toString() else "-"

        }
    }

    private fun initListener() {
        binding.apply {
            storeVisitBtnReset.setOnClickListener {
                onRestart()
            }

            storeVisitBtnCamera.setOnClickListener {
                startTakePhoto()
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
//              Silakan gunakan kode ini jika mengalami perubahan rotasi
                rotateFile(file)
                binding.storeVisitIvBackground.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    companion object {
        const val STORE = "store id"

        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}