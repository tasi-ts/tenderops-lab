# Kubernetes

This directory contains the raw Kubernetes manifests used earlier in the project.

The active deployment path is now:

```text
Helm chart → Argo CD → Kubernetes
```

The raw manifests are kept as learning material because they show the Kubernetes objects directly without Helm templating.

## Contents

- `base/namespace.yaml`
- `base/api.yaml`
- `base/postgres.yaml`

## Manual deployment

These files can be applied manually with:

```bash
kubectl apply -f k8s/base/namespace.yaml
kubectl apply -f k8s/base/postgres.yaml
kubectl apply -f k8s/base/api.yaml
```

For normal project use, prefer the Helm/Argo CD path.
