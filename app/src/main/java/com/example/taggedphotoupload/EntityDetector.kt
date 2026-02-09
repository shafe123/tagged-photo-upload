package com.example.taggedphotoupload

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.tasks.await
import java.io.File
import kotlin.math.abs

class EntityDetector(private val context: Context) {
    
    private val configManager = ConfigManager(context)
    
    // Configure face detector for better accuracy
    private val faceDetectorOptions = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .setMinFaceSize(0.15f)
        .enableTracking()
        .build()
    
    private val faceDetector = FaceDetection.getClient(faceDetectorOptions)
    
    /**
     * Detects if the target entity (from reference image) is present in the given image
     */
    suspend fun detectEntity(imageFile: File): Boolean {
        try {
            val referenceImagePath = configManager.getReferenceImagePath()
            if (referenceImagePath.isNullOrEmpty()) {
                Log.w("EntityDetector", "No reference image set")
                return false
            }
            
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            if (bitmap == null) {
                Log.e("EntityDetector", "Failed to decode image file")
                return false
            }
            
            val referenceBitmap = BitmapFactory.decodeFile(referenceImagePath)
            if (referenceBitmap == null) {
                Log.e("EntityDetector", "Failed to decode reference image")
                return false
            }
            
            return compareFaces(bitmap, referenceBitmap)
        } catch (e: Exception) {
            Log.e("EntityDetector", "Error detecting entity", e)
            return false
        }
    }
    
    /**
     * Detects if the target entity is present in the given bitmap
     */
    suspend fun detectEntityInBitmap(bitmap: Bitmap): Boolean {
        try {
            val referenceImagePath = configManager.getReferenceImagePath()
            if (referenceImagePath.isNullOrEmpty()) {
                Log.w("EntityDetector", "No reference image set")
                return false
            }
            
            val referenceBitmap = BitmapFactory.decodeFile(referenceImagePath)
            if (referenceBitmap == null) {
                Log.e("EntityDetector", "Failed to decode reference image")
                return false
            }
            
            return compareFaces(bitmap, referenceBitmap)
        } catch (e: Exception) {
            Log.e("EntityDetector", "Error detecting entity in bitmap", e)
            return false
        }
    }
    
    /**
     * Compares faces in two images to determine if they contain the same entity
     * This is a simplified comparison - in production, you'd want more sophisticated matching
     */
    private suspend fun compareFaces(image: Bitmap, referenceImage: Bitmap): Boolean {
        try {
            // Detect faces in both images
            val inputImage = InputImage.fromBitmap(image, 0)
            val referenceInputImage = InputImage.fromBitmap(referenceImage, 0)
            
            val faces = faceDetector.process(inputImage).await()
            val referenceFaces = faceDetector.process(referenceInputImage).await()
            
            if (faces.isEmpty() || referenceFaces.isEmpty()) {
                Log.d("EntityDetector", "No faces detected in one or both images")
                return false
            }
            
            // For simplicity, we'll compare the first face in each image
            // In a production app, you'd want to compare all faces and use more sophisticated matching
            val face = faces.first()
            val referenceFace = referenceFaces.first()
            
            // Compare face characteristics
            // This is a simple comparison - you'd want to use face embeddings/vectors in production
            val similarityScore = calculateFaceSimilarity(face.boundingBox.width().toFloat(), 
                                                         face.boundingBox.height().toFloat(),
                                                         referenceFace.boundingBox.width().toFloat(),
                                                         referenceFace.boundingBox.height().toFloat())
            
            val threshold = 0.7f // 70% similarity threshold
            val isMatch = similarityScore > threshold
            
            Log.d("EntityDetector", "Face similarity score: $similarityScore, Match: $isMatch")
            return isMatch
            
        } catch (e: Exception) {
            Log.e("EntityDetector", "Error comparing faces", e)
            return false
        }
    }
    
    /**
     * Calculate a simple similarity score based on face dimensions
     * In production, use proper face recognition/embedding comparison
     */
    private fun calculateFaceSimilarity(width1: Float, height1: Float, 
                                       width2: Float, height2: Float): Float {
        val widthRatio = minOf(width1, width2) / maxOf(width1, width2)
        val heightRatio = minOf(height1, height2) / maxOf(height1, height2)
        return (widthRatio + heightRatio) / 2f
    }
}
