package com.example.taggedphotoupload

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.azure.storage.blob.BlobClientBuilder
import com.azure.storage.blob.BlobContainerClient
import com.azure.storage.blob.BlobContainerClientBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AzureUploader(private val context: Context) {
    private val configManager = ConfigManager(context)
    
    suspend fun uploadImage(imageFile: File): Result<String> = withContext(Dispatchers.IO) {
        try {
            val accountName = configManager.getAccountName()
            val accountKey = configManager.getAccountKey()
            val containerName = configManager.getContainerName()
            
            if (accountName.isNullOrEmpty() || accountKey.isNullOrEmpty() || containerName.isNullOrEmpty()) {
                return@withContext Result.failure(Exception("Azure configuration not set"))
            }
            
            // Create connection string
            val connectionString = "DefaultEndpointsProtocol=https;" +
                    "AccountName=$accountName;" +
                    "AccountKey=$accountKey;" +
                    "EndpointSuffix=core.windows.net"
            
            // Create blob client
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val blobName = "photo_$timestamp.jpg"
            
            val blobClient = BlobClientBuilder()
                .connectionString(connectionString)
                .containerName(containerName)
                .blobName(blobName)
                .buildClient()
            
            // Upload the file
            val inputStream = imageFile.inputStream()
            blobClient.upload(inputStream, imageFile.length(), true)
            inputStream.close()
            
            Log.d("AzureUploader", "Successfully uploaded: $blobName")
            Result.success(blobName)
        } catch (e: Exception) {
            Log.e("AzureUploader", "Failed to upload image", e)
            Result.failure(e)
        }
    }
    
    suspend fun uploadBitmap(bitmap: Bitmap): Result<String> = withContext(Dispatchers.IO) {
        try {
            val accountName = configManager.getAccountName()
            val accountKey = configManager.getAccountKey()
            val containerName = configManager.getContainerName()
            
            if (accountName.isNullOrEmpty() || accountKey.isNullOrEmpty() || containerName.isNullOrEmpty()) {
                return@withContext Result.failure(Exception("Azure configuration not set"))
            }
            
            // Create connection string
            val connectionString = "DefaultEndpointsProtocol=https;" +
                    "AccountName=$accountName;" +
                    "AccountKey=$accountKey;" +
                    "EndpointSuffix=core.windows.net"
            
            // Convert bitmap to byte array
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            val imageBytes = outputStream.toByteArray()
            
            // Create blob client
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val blobName = "photo_$timestamp.jpg"
            
            val blobClient = BlobClientBuilder()
                .connectionString(connectionString)
                .containerName(containerName)
                .blobName(blobName)
                .buildClient()
            
            // Upload the bitmap
            val inputStream = ByteArrayInputStream(imageBytes)
            blobClient.upload(inputStream, imageBytes.size.toLong(), true)
            inputStream.close()
            
            Log.d("AzureUploader", "Successfully uploaded bitmap: $blobName")
            Result.success(blobName)
        } catch (e: Exception) {
            Log.e("AzureUploader", "Failed to upload bitmap", e)
            Result.failure(e)
        }
    }
}
