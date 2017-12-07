#!/bin/bash

echo "Running test cases..."
sbt test

echo "Running the app..."
sbt "runMain org.juanitodread.atlas.App"