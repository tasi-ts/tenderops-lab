#!/usr/bin/env bash
set -euo pipefail

mkdir -p reports/security

echo "1/4 Scanning repository filesystem..."
trivy fs \
  --scanners vuln,secret,misconfig \
  --severity HIGH,CRITICAL \
  --ignore-unfixed \
  --exit-code 0 \
  --format table \
  --output reports/security/trivy-fs.txt \
  .

echo "2/4 Building API image..."
docker build -t tenderops-api:0.1.0 ./src/api

echo "3/4 Scanning API container image..."
trivy image \
  --severity HIGH,CRITICAL \
  --ignore-unfixed \
  --exit-code 0 \
  --format table \
  --output reports/security/trivy-image.txt \
  tenderops-api:0.1.0

echo "4/4 Rendering Helm chart and scanning Kubernetes configuration..."
helm template tenderops charts/tenderops --namespace tenderops > reports/security/rendered-manifests.yaml

trivy config \
  --severity HIGH,CRITICAL \
  --exit-code 0 \
  --format table \
  --output reports/security/trivy-k8s-config.txt \
  reports/security/rendered-manifests.yaml

echo ""
echo "Security scan completed."
echo "Reports:"
echo "- reports/security/trivy-fs.txt"
echo "- reports/security/trivy-image.txt"
echo "- reports/security/trivy-k8s-config.txt"
