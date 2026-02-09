package com.example.taggedphotoupload

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.edit
import java.io.ByteArrayOutputStream
import java.io.File

class ConfigManager(context: Context) {
    private val prefs: SharedPreferences = 
        context.getSharedPreferences("TaggedPhotoConfig", Context.MODE_PRIVATE)
    
    companion object {
        private const val KEY_ACCOUNT_NAME = "azure_account_name"
        private const val KEY_ACCOUNT_KEY = "azure_account_key"
        private const val KEY_CONTAINER_NAME = "azure_container_name"
        private const val KEY_REFERENCE_IMAGE_PATH = "reference_image_path"
        private const val KEY_MONITORING_ENABLED = "monitoring_enabled"
        private const val KEY_UPLOAD_COUNT = "upload_count"
    }
    
    fun saveAzureConfig(accountName: String, accountKey: String, containerName: String) {
        prefs.edit {
            putString(KEY_ACCOUNT_NAME, accountName)
            putString(KEY_ACCOUNT_KEY, accountKey)
            putString(KEY_CONTAINER_NAME, containerName)
        }
    }
    
    fun getAccountName(): String? = prefs.getString(KEY_ACCOUNT_NAME, null)
    fun getAccountKey(): String? = prefs.getString(KEY_ACCOUNT_KEY, null)
    fun getContainerName(): String? = prefs.getString(KEY_CONTAINER_NAME, null)
    
    fun isAzureConfigured(): Boolean {
        return !getAccountName().isNullOrEmpty() && 
               !getAccountKey().isNullOrEmpty() && 
               !getContainerName().isNullOrEmpty()
    }
    
    fun saveReferenceImagePath(path: String) {
        prefs.edit {
            putString(KEY_REFERENCE_IMAGE_PATH, path)
        }
    }
    
    fun getReferenceImagePath(): String? = prefs.getString(KEY_REFERENCE_IMAGE_PATH, null)
    
    fun clearReferenceImage() {
        prefs.edit {
            remove(KEY_REFERENCE_IMAGE_PATH)
        }
    }
    
    fun hasReferenceImage(): Boolean = !getReferenceImagePath().isNullOrEmpty()
    
    fun setMonitoringEnabled(enabled: Boolean) {
        prefs.edit {
            putBoolean(KEY_MONITORING_ENABLED, enabled)
        }
    }
    
    fun isMonitoringEnabled(): Boolean = prefs.getBoolean(KEY_MONITORING_ENABLED, false)
    
    fun incrementUploadCount() {
        val current = getUploadCount()
        prefs.edit {
            putInt(KEY_UPLOAD_COUNT, current + 1)
        }
    }
    
    fun getUploadCount(): Int = prefs.getInt(KEY_UPLOAD_COUNT, 0)
}
