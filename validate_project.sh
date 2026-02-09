#!/bin/bash

# Android Project Structure Validation Script

echo "Validating Android project structure..."

# Check required files
required_files=(
    "build.gradle"
    "settings.gradle"
    "gradle.properties"
    "app/build.gradle"
    "app/src/main/AndroidManifest.xml"
    "app/src/main/res/values/strings.xml"
    "app/src/main/res/layout/activity_main.xml"
)

echo "Checking required files..."
all_files_exist=true
for file in "${required_files[@]}"; do
    if [ -f "$file" ]; then
        echo "✓ $file exists"
    else
        echo "✗ $file is missing"
        all_files_exist=false
    fi
done

# Check Kotlin source files
echo ""
echo "Checking Kotlin source files..."
kotlin_files=(
    "app/src/main/java/com/example/taggedphotoupload/MainActivity.kt"
    "app/src/main/java/com/example/taggedphotoupload/SetupActivity.kt"
    "app/src/main/java/com/example/taggedphotoupload/ConfigManager.kt"
    "app/src/main/java/com/example/taggedphotoupload/AzureUploader.kt"
    "app/src/main/java/com/example/taggedphotoupload/EntityDetector.kt"
    "app/src/main/java/com/example/taggedphotoupload/PhotoMonitorWorker.kt"
)

all_kotlin_exists=true
for file in "${kotlin_files[@]}"; do
    if [ -f "$file" ]; then
        echo "✓ $file exists"
    else
        echo "✗ $file is missing"
        all_kotlin_exists=false
    fi
done

# Check for common syntax errors in Kotlin files
echo ""
echo "Basic syntax validation..."
for file in "${kotlin_files[@]}"; do
    if [ -f "$file" ]; then
        # Check for package declaration
        if grep -q "^package com.example.taggedphotoupload" "$file"; then
            echo "✓ $file has correct package declaration"
        else
            echo "✗ $file missing or incorrect package declaration"
        fi
    fi
done

# Summary
echo ""
echo "===================="
if [ "$all_files_exist" = true ] && [ "$all_kotlin_exists" = true ]; then
    echo "✓ All required files are present"
    echo "✓ Project structure is valid"
    exit 0
else
    echo "✗ Some files are missing"
    exit 1
fi
