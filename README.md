# Tagged Photo Upload - Android App

An Android application that automatically detects and uploads photos containing a specific entity (e.g., a specific person, pet, or object) to Azure Blob Storage.

## Features

- **Entity-Based Photo Detection**: Use ML Kit Face Detection to identify photos containing a specific entity
- **Customizable Reference**: Users can select or capture a reference image of the entity they want to track
- **Azure Blob Storage Integration**: Automatically uploads matching photos to a configured Azure storage account
- **Background Monitoring**: Periodic background worker to check for new photos (every 15 minutes)
- **Simple Configuration**: Easy setup for Azure storage credentials
- **Privacy-Focused**: All processing happens on-device; only matching photos are uploaded

## Requirements

- Android 7.0 (API level 24) or higher
- Camera permission (for capturing reference images)
- Storage permission (for accessing photos)
- Internet permission (for uploading to Azure)
- An Azure Storage account with a blob container

## Setup Instructions

### 1. Azure Storage Account Setup

Before using the app, you need to create an Azure Storage account:

1. Go to [Azure Portal](https://portal.azure.com)
2. Create a new Storage Account (or use an existing one)
3. Create a new Blob Container in the storage account
4. Get your Storage Account Name and Access Key from the Azure Portal
   - Navigate to your Storage Account
   - Go to "Access keys" under Security + networking
   - Copy the "Storage account name" and one of the "Key" values

### 2. App Configuration

1. Launch the app on your Android device
2. Tap "Setup Azure Storage"
3. Enter your:
   - Azure Storage Account Name
   - Azure Account Key
   - Container Name
4. Tap "Save Configuration"

### 3. Set Reference Entity

1. On the main screen, either:
   - Tap "Select Reference Image" to choose an existing photo from your gallery
   - Tap "Capture Reference Image" to take a new photo
2. The app will use this image as the reference for detecting the entity

### 4. Start Monitoring

1. Tap "Start Monitoring" to begin automatic photo detection
2. The app will periodically check for new photos
3. Photos containing the detected entity will be automatically uploaded to Azure

## How It Works

1. **Reference Image**: The user provides a reference image containing the entity they want to track (e.g., their cat)
2. **Face Detection**: The app uses Google ML Kit's Face Detection to analyze faces in photos
3. **Similarity Matching**: When a new photo is found, the app compares faces in the photo with the reference image
4. **Automatic Upload**: If a match is found (above 70% similarity threshold), the photo is uploaded to Azure Blob Storage
5. **Background Processing**: A WorkManager periodic task runs every 15 minutes to check for new photos

## Technical Architecture

### Components

- **MainActivity**: Main user interface for managing reference images and monitoring
- **SetupActivity**: Configuration screen for Azure Storage credentials
- **ConfigManager**: Manages app configuration using SharedPreferences
- **EntityDetector**: Uses ML Kit Face Detection to identify matching entities
- **AzureUploader**: Handles uploading photos to Azure Blob Storage
- **PhotoMonitorWorker**: Background worker that periodically checks for new photos

### Dependencies

- **AndroidX Core & AppCompat**: Android framework libraries
- **Material Design Components**: Modern UI components
- **ML Kit Face Detection**: On-device face detection
- **Azure Storage Blob SDK**: Azure Blob Storage client
- **CameraX**: Camera functionality for capturing reference images
- **WorkManager**: Background task scheduling
- **Kotlin Coroutines**: Asynchronous programming

## Privacy & Security

- All entity detection happens on-device using ML Kit
- Azure credentials are stored locally in encrypted SharedPreferences
- Only photos that match the reference entity are uploaded
- No data is sent to third-party services except Azure Storage

## Limitations & Future Improvements

### Current Limitations

- Face detection is used as a proxy for entity detection (works well for people and pets with faces)
- Simplified similarity matching (production apps should use face embeddings)
- Manual monitoring interval (every 15 minutes via WorkManager)
- Basic reference image comparison

### Potential Improvements

1. **Better Entity Detection**:
   - Use ML Kit Object Detection for non-face entities
   - Implement face embedding comparison for more accurate matching
   - Support multiple reference images
   - Allow custom similarity thresholds

2. **Enhanced Photo Monitoring**:
   - Real-time monitoring using ContentObserver
   - Process photos immediately when they're taken
   - Background upload queue with retry logic
   - Batch uploads to save battery

3. **User Experience**:
   - Photo preview before upload
   - Upload history and management
   - Delete uploaded photos from device option
   - Progress notifications
   - Dark mode support

4. **Advanced Features**:
   - Multiple entity tracking
   - Cloud-based face recognition
   - Automatic photo organization
   - Share uploaded photos

## Building the App

### Prerequisites

- Android Studio Arctic Fox or later
- JDK 8 or higher
- Android SDK 34

### Build Steps

1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Build and run on an Android device or emulator

```bash
./gradlew assembleDebug
```

## Testing

To test the app without uploading to Azure:

1. Set up Azure credentials (even test credentials)
2. Select a reference image with a clear face
3. Take or select photos that contain similar faces
4. Monitor the logs to see detection results

## License

See LICENSE file for details.

## Support

For issues, questions, or contributions, please open an issue on the GitHub repository.
