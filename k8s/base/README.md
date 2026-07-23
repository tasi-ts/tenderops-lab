# Kubernetes Base Manifests

This folder contains plain Kubernetes YAML examples.

These files were used before the project was converted to Helm and Argo CD.

## Resources

The API manifest defines:

- Secret
- ConfigMap
- Deployment
- Service

The PostgreSQL manifest defines:

- Secret
- PersistentVolumeClaim
- Deployment
- Service

The namespace manifest defines:

- the `tenderops` namespace

## Security settings

The API and PostgreSQL manifests include basic container hardening:

- non-root execution
- dropped Linux capabilities
- disabled privilege escalation
- read-only root filesystem
- seccomp `RuntimeDefault`
- resource requests and limits for the API

## Current role

The active deployment path is now:

```text
Helm chart → Argo CD → Kubernetes
```

These raw manifests are kept as learning material because they show what Helm renders behind the scenes.
