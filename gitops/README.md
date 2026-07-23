# GitOps

This directory contains GitOps configuration for TenderOps Lab.

The project uses Argo CD to watch the GitHub repository and synchronize the Helm chart into the local Kubernetes cluster.

## Current flow

```text
GitHub main branch
  |
  v
Argo CD Application
  |
  v
charts/tenderops Helm chart
  |
  v
tenderops namespace in Kubernetes
```

## Why GitOps?

GitOps makes Git the source of truth for deployment state.

Instead of manually running `kubectl apply` or `helm install`, the desired state is committed to Git. Argo CD compares that desired state with the live cluster and reconciles any difference.

## Current Argo CD status check

```bash
kubectl get applications -n argocd
```

Expected result:

```text
NAME        SYNC STATUS   HEALTH STATUS
tenderops   Synced        Healthy
```

## Important note

Do not manually reinstall the TenderOps Helm release during normal use.

Preferred flow:

```text
Edit chart or values
  |
  v
Commit and push
  |
  v
Argo CD syncs
```
