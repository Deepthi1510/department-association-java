#!/bin/bash
# Build script for Unix/Linux/macOS
# Compiles Java sources and creates runnable JAR

SRC_DIR="src"
OUT_DIR="out"
LIB_DIR="lib"
RESOURCES_DIR="resources"

# Build classpath from lib directory
CLASSPATH="."
for jar in "$LIB_DIR"/*.jar; do
    CLASSPATH="$CLASSPATH:$jar"
done

# Create output directories
mkdir -p "$OUT_DIR"
mkdir -p "$OUT_DIR/lib"

echo "Cleaning previous builds..."
rm -rf "$OUT_DIR/com" "$OUT_DIR"/*.class

echo "Compiling Java sources..."
javac -d "$OUT_DIR" -cp "$CLASSPATH" \
    "$SRC_DIR"/com/deptassoc/*.java \
    "$SRC_DIR"/com/deptassoc/db/*.java \
    "$SRC_DIR"/com/deptassoc/dao/*.java \
    "$SRC_DIR"/com/deptassoc/model/*.java \
    "$SRC_DIR"/com/deptassoc/auth/*.java \
    "$SRC_DIR"/com/deptassoc/ui/*.java \
    "$SRC_DIR"/com/deptassoc/ui/faculty/*.java \
    "$SRC_DIR"/com/deptassoc/ui/association/*.java \
    "$SRC_DIR"/com/deptassoc/swingui/*.java \
    "$SRC_DIR"/com/deptassoc/util/*.java

if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi

echo "Copying configuration files..."
cp "$RESOURCES_DIR"/*.properties "$OUT_DIR/"

echo "Copying library files..."
cp "$LIB_DIR"/*.jar "$OUT_DIR/lib/"

echo "Creating manifest file..."
cat > "$OUT_DIR/MANIFEST.MF" << EOF
Manifest-Version: 1.0
Main-Class: com.deptassoc.Main
Class-Path: lib/*
EOF

echo "Creating JAR file..."
cd "$OUT_DIR"
jar cfm app.jar MANIFEST.MF com/ *.properties
cd ..

echo ""
echo "Build complete!"
echo "Runnable JAR created: $OUT_DIR/app.jar"
echo "To run: java -jar $OUT_DIR/app.jar"
echo ""
