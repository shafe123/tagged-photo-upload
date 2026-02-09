# Developer Guide - Tagged Photo Upload App

## Quick Start Guide

### Prerequisites
- Android Studio Arctic Fox (2020.3.1) or later
- JDK 8 or higher
- Android SDK API 34
- An Android device or emulator running Android 7.0+ (API 24+)
- Azure Storage Account (for testing uploads)

### Building the App

1. **Clone the repository**
   ```bash
   git clone https://github.com/shafe123/tagged-photo-upload.git
   cd tagged-photo-upload
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory

3. **Sync Gradle**
   - Android Studio will automatically prompt to sync Gradle files
   - Wait for dependencies to download

4. **Build the project**
   ```bash
   ./gradlew build
   ```

5. **Run on device/emulator**
   - Connect an Android device or start an emulator
   - Click the "Run" button in Android Studio
   - Or use command line:
   ```bash
   ./gradlew installDebug
   ```

## Project Structure

```
tagged-photo-upload/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/taggedphotoupload/
│   │   │   │   ├── MainActivity.kt          # Main UI screen
│   │   │   │   ├── SetupActivity.kt         # Azure configuration screen
│   │   │   │   ├── ConfigManager.kt         # SharedPreferences manager
│   │   │   │   ├── AzureUploader.kt         # Azure Blob Storage client
│   │   │   │   ├── EntityDetector.kt        # ML Kit face detection
│   │   │   │   └── PhotoMonitorWorker.kt    # Background monitoring worker
│   │   │   ├── res/
│   │   │   │   ├── layout/                  # UI layouts
│   │   │   │   ├── values/                  # Strings, colors, themes
│   │   │   │   └── xml/                     # File provider paths
│   │   │   └── AndroidManifest.xml          # App manifest
│   │   └── build.gradle                     # App-level build config
│   └── build.gradle                         # Project-level build config
├── gradle/                                  # Gradle wrapper
├── README.md                                # User documentation
├── SECURITY.md                              # Security analysis
└── settings.gradle                          # Project settings
```

## Architecture Overview

### Component Diagram
```
┌─────────────────────────────────────────────────────┐
│                   MainActivity                       │
│  - User Interface                                    │
│  - Reference Image Selection                        │
│  - Monitoring Control                               │
└────────┬────────────────────────────────────────────┘
         │
         ├──► ConfigManager
         │    - SharedPreferences
         │    - Settings storage
         │
         ├──► EntityDetector
         │    - ML Kit Face Detection
         │    - Face comparison
         │
         ├──► AzureUploader
         │    - Azure Blob Storage SDK
         │    - Photo uploads
         │
         └──► PhotoMonitorWorker (WorkManager)
              - Periodic background task
              - Photo scanning
              - Auto-upload matching photos
```

### Data Flow

1. **Setup Flow**:
   ```
   User → SetupActivity → ConfigManager → SharedPreferences
   ```

2. **Reference Image Flow**:
   ```
   User → Gallery/Camera → MainActivity → File Storage → ConfigManager
   ```

3. **Monitoring Flow**:
   ```
   User starts monitoring → WorkManager schedules PhotoMonitorWorker
   → Worker checks new photos → EntityDetector analyzes faces
   → If match found → AzureUploader uploads to Azure
   ```

## Key Classes

### MainActivity
**Purpose**: Main user interface for the app

**Key Methods**:
- `selectReferenceImage()`: Opens gallery to select reference photo
- `captureReferenceImage()`: Opens camera to capture reference photo
- `toggleMonitoring()`: Starts/stops photo monitoring
- `updateUI()`: Refreshes UI based on current state

### ConfigManager
**Purpose**: Manages app configuration and settings

**Key Methods**:
- `saveAzureConfig()`: Stores Azure credentials
- `isAzureConfigured()`: Checks if Azure is set up
- `saveReferenceImagePath()`: Stores reference image location
- `setMonitoringEnabled()`: Controls monitoring state

### EntityDetector
**Purpose**: Detects if target entity is in a photo using ML Kit

**Key Methods**:
- `detectEntity(File)`: Analyzes a photo file
- `detectEntityInBitmap(Bitmap)`: Analyzes a bitmap
- `compareFaces()`: Compares faces for similarity

**Detection Algorithm**:
1. Use ML Kit to detect faces in both images
2. Extract face characteristics (bounding box dimensions)
3. Calculate similarity score
4. Compare against threshold (70%)

### AzureUploader
**Purpose**: Uploads photos to Azure Blob Storage

**Key Methods**:
- `uploadImage(File)`: Uploads image file to Azure
- `uploadBitmap(Bitmap)`: Uploads bitmap to Azure

**Connection Flow**:
1. Build connection string from stored credentials
2. Create BlobClient for target container
3. Upload image with timestamp-based filename

### PhotoMonitorWorker
**Purpose**: Background worker that checks for new photos

**Execution**:
- Runs every 15 minutes (configurable)
- Only when network is available
- Checks for monitoring enabled state
- In current implementation: placeholder for actual photo scanning

**Future Enhancement**:
Would query MediaStore for new images and process them.

## Configuration

### SharedPreferences Keys
```kotlin
// Azure Configuration
"azure_account_name"    // String: Azure storage account name
"azure_account_key"     // String: Azure access key
"azure_container_name"  // String: Target container name

// App State
"reference_image_path"  // String: Path to reference image file
"monitoring_enabled"    // Boolean: Is monitoring active?
"upload_count"         // Int: Number of photos uploaded
```

### File Storage Locations
- Reference images: `<app_external_files_dir>/reference_image.jpg`
- Temporary captures: `<app_external_files_dir>/reference_<timestamp>.jpg`

## Dependencies

### Core Android
- `androidx.core:core-ktx` - Kotlin extensions
- `androidx.appcompat:appcompat` - Backward compatibility
- `com.google.android.material:material` - Material Design UI

### Azure
- `com.azure:azure-storage-blob:12.25.0` - Azure Blob Storage client

### ML Kit
- `com.google.mlkit:face-detection:16.1.6` - On-device face detection

### Background Work
- `androidx.work:work-runtime-ktx:2.9.0` - Background job scheduler

### Camera
- `androidx.camera:camera-*` - CameraX library for photo capture

## Testing

### Manual Testing Checklist

1. **Setup**:
   - [ ] Open app for first time
   - [ ] Navigate to Setup
   - [ ] Enter Azure credentials
   - [ ] Save configuration
   - [ ] Verify config saved message

2. **Reference Image**:
   - [ ] Select image from gallery
   - [ ] Verify image displayed
   - [ ] Capture new image
   - [ ] Verify captured image displayed
   - [ ] Clear reference image
   - [ ] Verify image cleared

3. **Monitoring**:
   - [ ] Try to start monitoring without setup (should show alert)
   - [ ] Try to start monitoring without reference (should show toast)
   - [ ] Start monitoring with valid setup
   - [ ] Verify status changes
   - [ ] Stop monitoring
   - [ ] Verify status changes

4. **Permissions**:
   - [ ] Grant camera permission
   - [ ] Grant storage permission
   - [ ] Test without permissions (should request)

### Unit Testing (Future)
Recommended test coverage:
- ConfigManager: Test all get/set operations
- EntityDetector: Mock ML Kit, test similarity calculations
- AzureUploader: Mock Azure client, test upload logic

### Integration Testing (Future)
- End-to-end flow from reference image to upload
- WorkManager scheduling
- Permission handling

## Debugging

### Enable Verbose Logging
Check LogCat for these tags:
- `EntityDetector` - Face detection results
- `AzureUploader` - Upload status
- `PhotoMonitorWorker` - Monitoring execution
- `MainActivity` - UI events

### Common Issues

**Issue**: App crashes on image selection
**Solution**: Check storage permissions granted

**Issue**: Monitoring doesn't start
**Solution**: Verify Azure credentials and reference image are set

**Issue**: Photos not uploading
**Solution**: Check internet connection, verify Azure credentials

**Issue**: Face detection not working
**Solution**: Ensure reference image has clear, visible face

## Customization

### Adjust Face Similarity Threshold
In `EntityDetector.kt`:
```kotlin
companion object {
    private const val FACE_SIMILARITY_THRESHOLD = 0.7f  // Change this value
}
```
- Higher values (e.g., 0.9): More strict matching
- Lower values (e.g., 0.5): More permissive matching

### Change Monitoring Interval
In `MainActivity.kt`:
```kotlin
val workRequest = PeriodicWorkRequestBuilder<PhotoMonitorWorker>(
    15, TimeUnit.MINUTES  // Change interval here
)
```

### Customize Upload Filename
In `AzureUploader.kt`:
```kotlin
val blobName = "photo_$timestamp.jpg"  // Customize naming
```

## Production Considerations

### Before Production Release:

1. **Security Enhancements**:
   - Implement EncryptedSharedPreferences for credentials
   - Use Azure SAS tokens instead of account keys
   - Add certificate pinning

2. **Performance**:
   - Implement actual MediaStore monitoring
   - Add upload queue and retry logic
   - Optimize face detection for battery life

3. **User Experience**:
   - Add progress indicators for uploads
   - Implement notification for upload status
   - Add upload history view

4. **Error Handling**:
   - Better error messages
   - Offline queue for uploads
   - Network error recovery

5. **Testing**:
   - Add unit tests
   - Add integration tests
   - Test on multiple devices and Android versions

6. **Code Quality**:
   - Run Android Lint
   - Fix all warnings
   - Add KDoc comments

## Contributing

When contributing to this project:
1. Follow Kotlin coding conventions
2. Add comments for complex logic
3. Update this guide if adding new features
4. Test on multiple Android versions
5. Ensure no security regressions

## License

See LICENSE file in repository root.
