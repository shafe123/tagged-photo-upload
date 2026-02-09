package com.example.taggedphotoupload

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    
    private lateinit var configManager: ConfigManager
    private lateinit var entityDetector: EntityDetector
    private lateinit var azureUploader: AzureUploader
    
    private lateinit var statusLabel: TextView
    private lateinit var referenceImageView: ImageView
    private lateinit var referenceLabel: TextView
    private lateinit var selectReferenceButton: Button
    private lateinit var captureReferenceButton: Button
    private lateinit var clearReferenceButton: Button
    private lateinit var setupButton: Button
    private lateinit var toggleMonitoringButton: Button
    private lateinit var uploadCountText: TextView
    
    private var currentPhotoUri: Uri? = null
    
    // Permission request codes
    private val PERMISSION_REQUEST_CODE = 100
    
    // Activity result launchers
    private val selectImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                handleSelectedImage(uri)
            }
        }
    }
    
    private val captureImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            currentPhotoUri?.let { uri ->
                handleCapturedImage(uri)
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize managers
        configManager = ConfigManager(this)
        entityDetector = EntityDetector(this)
        azureUploader = AzureUploader(this)
        
        // Initialize views
        initializeViews()
        
        // Set up button listeners
        setupButtonListeners()
        
        // Request permissions
        requestPermissions()
        
        // Update UI
        updateUI()
    }
    
    override fun onResume() {
        super.onResume()
        updateUI()
    }
    
    private fun initializeViews() {
        statusLabel = findViewById(R.id.statusLabel)
        referenceImageView = findViewById(R.id.referenceImageView)
        referenceLabel = findViewById(R.id.referenceLabel)
        selectReferenceButton = findViewById(R.id.selectReferenceButton)
        captureReferenceButton = findViewById(R.id.captureReferenceButton)
        clearReferenceButton = findViewById(R.id.clearReferenceButton)
        setupButton = findViewById(R.id.setupButton)
        toggleMonitoringButton = findViewById(R.id.toggleMonitoringButton)
        uploadCountText = findViewById(R.id.uploadCountText)
    }
    
    private fun setupButtonListeners() {
        selectReferenceButton.setOnClickListener {
            selectReferenceImage()
        }
        
        captureReferenceButton.setOnClickListener {
            captureReferenceImage()
        }
        
        clearReferenceButton.setOnClickListener {
            clearReferenceImage()
        }
        
        setupButton.setOnClickListener {
            openSetup()
        }
        
        toggleMonitoringButton.setOnClickListener {
            toggleMonitoring()
        }
    }
    
    private fun requestPermissions() {
        val permissions = mutableListOf<String>()
        
        // Camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
            != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA)
        }
        
        // Storage permissions based on Android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        
        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }
    
    private fun selectReferenceImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        selectImageLauncher.launch(intent)
    }
    
    private fun captureReferenceImage() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        
        // Create a file to save the image
        val photoFile = File(getExternalFilesDir(null), "reference_${System.currentTimeMillis()}.jpg")
        currentPhotoUri = FileProvider.getUriForFile(
            this,
            "${applicationContext.packageName}.fileprovider",
            photoFile
        )
        
        intent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)
        captureImageLauncher.launch(intent)
    }
    
    private fun handleSelectedImage(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            
            if (bitmap != null) {
                saveReferenceImage(bitmap)
            } else {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading image: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun handleCapturedImage(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            
            if (bitmap != null) {
                saveReferenceImage(bitmap)
            } else {
                Toast.makeText(this, "Failed to load captured image", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading captured image: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun saveReferenceImage(bitmap: Bitmap) {
        try {
            val referenceFile = File(getExternalFilesDir(null), "reference_image.jpg")
            val outputStream = FileOutputStream(referenceFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.close()
            
            configManager.saveReferenceImagePath(referenceFile.absolutePath)
            updateUI()
            
            Toast.makeText(this, "Reference image saved", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error saving reference image: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun clearReferenceImage() {
        AlertDialog.Builder(this)
            .setTitle("Clear Reference Image")
            .setMessage("Are you sure you want to clear the reference image?")
            .setPositiveButton("Yes") { _, _ ->
                configManager.clearReferenceImage()
                updateUI()
                Toast.makeText(this, "Reference image cleared", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No", null)
            .show()
    }
    
    private fun openSetup() {
        val intent = Intent(this, SetupActivity::class.java)
        startActivity(intent)
    }
    
    private fun toggleMonitoring() {
        if (!configManager.isAzureConfigured()) {
            AlertDialog.Builder(this)
                .setTitle(R.string.setup_required)
                .setMessage(R.string.setup_message)
                .setPositiveButton(R.string.go_to_setup) { _, _ ->
                    openSetup()
                }
                .setNegativeButton("Cancel", null)
                .show()
            return
        }
        
        if (!configManager.hasReferenceImage()) {
            Toast.makeText(this, "Please set a reference image first", Toast.LENGTH_SHORT).show()
            return
        }
        
        val isEnabled = configManager.isMonitoringEnabled()
        configManager.setMonitoringEnabled(!isEnabled)
        
        if (!isEnabled) {
            startPhotoMonitoring()
        } else {
            stopPhotoMonitoring()
        }
        
        updateUI()
    }
    
    private fun startPhotoMonitoring() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        
        val workRequest = PeriodicWorkRequestBuilder<PhotoMonitorWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()
        
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            PhotoMonitorWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
        
        Toast.makeText(this, "Photo monitoring started", Toast.LENGTH_SHORT).show()
    }
    
    private fun stopPhotoMonitoring() {
        WorkManager.getInstance(this).cancelUniqueWork(PhotoMonitorWorker.WORK_NAME)
        Toast.makeText(this, "Photo monitoring stopped", Toast.LENGTH_SHORT).show()
    }
    
    private fun updateUI() {
        // Update monitoring status
        val isMonitoring = configManager.isMonitoringEnabled()
        statusLabel.text = if (isMonitoring) {
            getString(R.string.monitoring_active)
        } else {
            getString(R.string.monitoring_inactive)
        }
        
        toggleMonitoringButton.text = if (isMonitoring) {
            getString(R.string.stop_monitoring)
        } else {
            getString(R.string.start_monitoring)
        }
        
        // Update reference image
        val referenceImagePath = configManager.getReferenceImagePath()
        if (!referenceImagePath.isNullOrEmpty()) {
            val bitmap = BitmapFactory.decodeFile(referenceImagePath)
            if (bitmap != null) {
                referenceImageView.setImageBitmap(bitmap)
                referenceLabel.text = getString(R.string.reference_entity_set)
            } else {
                referenceImageView.setImageResource(0)
                referenceLabel.text = getString(R.string.no_reference_set)
            }
        } else {
            referenceImageView.setImageResource(0)
            referenceLabel.text = getString(R.string.no_reference_set)
        }
        
        // Update upload count
        val uploadCount = configManager.getUploadCount()
        uploadCountText.text = if (uploadCount > 0) {
            getString(R.string.upload_count, uploadCount)
        } else {
            getString(R.string.no_uploads)
        }
    }
}
