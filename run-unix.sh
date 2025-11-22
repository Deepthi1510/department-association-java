#!/bin/bash
# Run script for Unix/Linux/macOS
# Runs the application using JAR file

if [ ! -f "out/app.jar" ]; then
    echo "ERROR: out/app.jar not found!"
    echo "Please run build-unix.sh first."
    exit 1
fi

echo "Starting Department Association Management System..."
echo ""

java -jar out/app.jar
