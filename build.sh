#!/bin/bash

# StatsHolograms Build Script
# This script builds the plugin JAR file using Maven

echo "======================================"
echo "  StatsHolograms Build Script"
echo "======================================"
echo ""

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Error: Maven is not installed!"
    echo "Please install Maven from https://maven.apache.org/"
    exit 1
fi

echo "✓ Maven found"
echo ""

# Check Java version
echo "Checking Java version..."
java -version 2>&1 | head -n 1

echo ""
echo "Starting build..."
echo ""

# Clean and package
mvn clean package

# Check if build was successful
if [ $? -eq 0 ]; then
    echo ""
    echo "======================================"
    echo "  ✅ Build Successful!"
    echo "======================================"
    echo ""
    echo "📦 Plugin JAR location:"
    echo "   target/StatsHolograms-1.0.0.jar"
    echo ""
    echo "📋 Next steps:"
    echo "   1. Copy the JAR to your server's plugins/ folder"
    echo "   2. Restart your server"
    echo "   3. Run /hologram to create holograms!"
    echo ""
else
    echo ""
    echo "======================================"
    echo "  ❌ Build Failed!"
    echo "======================================"
    echo ""
    echo "Please check the error messages above."
    echo ""
    exit 1
fi
