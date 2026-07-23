# Helm Charts

This directory contains the Helm chart used to deploy TenderOps Lab.

## Chart

```text
charts/tenderops
```

The chart deploys:

- Spring Boot API
- PostgreSQL database
- Kubernetes Secrets
- Kubernetes ConfigMaps
- Kubernetes Services
- PersistentVolumeClaim for PostgreSQL
- readiness and liveness probes
- resource requests and limits
- basic container security contexts

## Useful commands

Render the chart locally:

```bash
helm template tenderops charts/tenderops --namespace tenderops
```

Validate the chart:

```bash
helm lint charts/tenderops
```

Install manually:

```bash
helm install tenderops charts/tenderops --namespace tenderops --create-namespace
```

Upgrade manually:

```bash
helm upgrade tenderops charts/tenderops --namespace tenderops
```

## Current role

In the current project, Argo CD manages this Helm chart from Git.

The preferred deployment flow is:

```text
Change Helm chart or values
  |
  v
Commit and push to GitHub
  |
  v
Argo CD syncs the change into Kubernetes
```
