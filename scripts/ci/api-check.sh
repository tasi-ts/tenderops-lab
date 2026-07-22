#!/usr/bin/env bash
set -euo pipefail

echo "Running API tests..."
pushd src/api > /dev/null
./mvnw clean test
popd > /dev/null

echo "Building API Docker image..."
docker build -t tenderops-api:ci ./src/api

echo "API CI checks completed successfully."
