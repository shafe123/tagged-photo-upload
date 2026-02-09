# Quick Start Guide - Tagged Photo Upload

Welcome! This guide will help you set up and use the Tagged Photo Upload app in just a few minutes.

## What Does This App Do?

This app automatically finds and uploads photos that contain a specific person, pet, or object to your Azure cloud storage. For example:
- Upload all photos that have your cat in them
- Upload all photos that have your child in them
- Upload all photos from events with specific people

## Before You Start

You'll need:
1. An Android phone (Android 7.0 or higher)
2. An Azure Storage Account (see setup below)

## Step 1: Set Up Azure Storage (5 minutes)

### Create Your Azure Storage Account

1. Go to [Azure Portal](https://portal.azure.com)
2. Sign in (or create a free account)
3. Click "Create a resource"
4. Search for "Storage Account" and click "Create"
5. Fill in the basics:
   - **Subscription**: Choose your subscription
   - **Resource Group**: Create new or use existing
   - **Storage account name**: Choose a unique name (e.g., "myphotos123")
   - **Region**: Choose closest to you
   - **Performance**: Standard
   - **Redundancy**: LRS (cheapest option)
6. Click "Review + Create", then "Create"
7. Wait for deployment (1-2 minutes)

### Create a Container

1. After deployment, click "Go to resource"
2. In the left menu, click "Containers" (under Data storage)
3. Click "+ Container" at the top
4. Name your container (e.g., "tagged-photos")
5. Keep "Private" access level
6. Click "Create"

### Get Your Access Keys

1. In the left menu, click "Access keys" (under Security + networking)
2. You'll see:
   - **Storage account name**: Copy this
   - **Key1**: Click "Show" and copy the key value

**Important**: Keep these safe! Don't share them.

## Step 2: Install the App

1. Download and install the app on your Android device
2. Open the app

## Step 3: Configure the App (2 minutes)

1. When you first open the app, tap "**Setup Azure Storage**"
2. Enter the information you copied:
   - **Azure Storage Account Name**: Your storage account name (e.g., "myphotos123")
   - **Azure Account Key**: The Key1 value you copied
   - **Container Name**: Your container name (e.g., "tagged-photos")
3. Tap "**Save Configuration**"
4. You'll see "Configuration saved successfully"
5. The app will return to the main screen

## Step 4: Select Your Reference Entity (1 minute)

Now tell the app what to look for by providing a reference image:

### Option A: Use an Existing Photo
1. Tap "**Select Reference Image**"
2. Choose a clear photo from your gallery
3. Pick one with the person/pet/object clearly visible
4. The image will appear in the app

### Option B: Take a New Photo
1. Tap "**Capture Reference Image**"
2. Take a clear photo of your target (person, pet, etc.)
3. Make sure the subject is well-lit and clearly visible
4. The image will appear in the app

**Tips for Best Results**:
- Use a photo with good lighting
- Make sure the face/subject is clearly visible
- Avoid blurry or distant photos
- For pets, make sure their face is visible

## Step 5: Start Monitoring (30 seconds)

1. Tap "**Start Monitoring**"
2. Grant any permissions if requested:
   - Camera (for taking reference photos)
   - Storage (for accessing photos)
3. You'll see "Photo monitoring started"
4. The status will change to "Photo monitoring is active"

**That's it!** The app is now running in the background.

## How It Works

Once monitoring is active:

1. **Every 15 minutes**, the app checks for new photos on your device
2. **For each new photo**, it uses AI to detect if your target entity is present
3. **If a match is found** (above 70% similarity), the photo is automatically uploaded to Azure
4. **Upload count** is displayed at the bottom of the screen

The app works in the background - you don't need to keep it open!

## Managing the App

### Check Upload Status
- Open the app to see how many photos have been uploaded
- The number appears at the bottom: "Photos uploaded: X"

### Stop Monitoring
1. Open the app
2. Tap "**Stop Monitoring**"
3. The app will stop checking for new photos

### Change Reference Entity
1. Open the app
2. Tap "**Clear Reference**"
3. Select or capture a new reference image
4. Monitoring will automatically use the new reference

### View Uploaded Photos
1. Go to [Azure Portal](https://portal.azure.com)
2. Navigate to your Storage Account
3. Click "Containers"
4. Click your container name
5. You'll see all uploaded photos with timestamps

### Download Photos from Azure
In the Azure Portal (in your container):
1. Click on a photo
2. Click "Download" at the top
3. The photo will download to your computer

## Troubleshooting

### "Setup Required" message
- You need to configure Azure credentials first
- Tap "Go to Setup" and enter your Azure details

### "No reference image set"
- You need to select or capture a reference image
- Tap "Select Reference Image" or "Capture Reference Image"

### Photos not uploading
- Check your internet connection
- Verify Azure credentials are correct
- Make sure monitoring is started
- Ensure reference image has a clear face/subject

### "Failed to upload photo"
- Check internet connection
- Verify Azure account has enough storage space
- Check that container name is correct

### Monitoring not working
- Ensure all permissions are granted
- Check that battery optimization isn't blocking the app
- Verify monitoring status shows "active"

## Privacy & Security

**Your Privacy**:
- All face detection happens on your device
- No data is sent to third parties
- Only matching photos are uploaded to YOUR Azure storage
- You control what gets uploaded by choosing the reference image

**Your Data**:
- Photos are uploaded to YOUR Azure account
- You have complete control and ownership
- Delete photos anytime from Azure Portal
- Stop monitoring anytime

## Tips for Best Results

### Reference Image Quality
✅ DO:
- Use clear, well-lit photos
- Ensure subject faces the camera
- Use recent photos
- Choose photos with good focus

❌ DON'T:
- Use blurry or dark photos
- Use photos where subject is very small
- Use heavily filtered photos
- Use photos with multiple people (choose one with clear subject)

### Monitoring
- Keep your phone charged
- Ensure stable internet for uploads
- Check upload count occasionally
- Update reference image if appearance changes significantly

### Azure Storage
- Monitor your Azure costs (storage is typically very cheap)
- Organize photos with custom naming (future feature)
- Set up retention policies if needed
- Back up important photos elsewhere too

## Cost Considerations

**Azure Storage Pricing** (approximate, as of 2024):
- Storage: ~$0.02 per GB per month
- Uploads: First 5GB free per month
- For 1000 photos (~2GB): About $0.04/month

**Example Monthly Costs**:
- 100 photos: < $0.01
- 1,000 photos: ~$0.04
- 10,000 photos: ~$0.40

Always check current Azure pricing for your region.

## Getting Help

### Common Questions

**Q: Can I have multiple reference entities?**
A: Currently, only one reference at a time. Clear and set a new one to switch.

**Q: How accurate is the detection?**
A: The app uses Google's ML Kit with 70% similarity threshold. Works best with clear faces.

**Q: Does this drain my battery?**
A: Minimal impact. The app runs briefly every 15 minutes when connected to internet.

**Q: Can I change the detection sensitivity?**
A: Advanced users can modify the FACE_SIMILARITY_THRESHOLD in the code (see Developer Guide).

**Q: Will this upload my entire photo library?**
A: No! Only NEW photos (after you start monitoring) that match your reference will be uploaded.

### Need More Help?

- Check the README.md for detailed information
- Review the DEVELOPER_GUIDE.md for technical details
- Open an issue on GitHub for bugs or questions

## Next Steps

Now that you're set up:
1. ✅ Take some test photos with your target entity
2. ✅ Wait 15 minutes and check if they uploaded
3. ✅ View them in Azure Portal
4. ✅ Adjust your reference image if needed

Enjoy automatic photo organization! 📸☁️
