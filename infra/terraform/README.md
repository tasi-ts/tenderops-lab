# Terraform

This directory is reserved for future Terraform examples.

Terraform is not required for the current local-first version of TenderOps Lab.

## Intended future scope

A future Terraform version could define:

- Azure resource group
- Azure Container Registry
- Azure Kubernetes Service
- Azure Database for PostgreSQL
- Key Vault
- Log Analytics workspace

## Role in the architecture

Terraform would create and manage the cloud infrastructure platform.

Kubernetes, Helm, and Argo CD would then deploy the application onto that platform.

## Relationship to the current lab

Current local version:

```text
Docker Desktop / kind
  |
  v
Local Kubernetes cluster
  |
  v
Helm + Argo CD deployment
```

Possible future Azure version:

```text
Terraform
  |
  v
Azure infrastructure
  |
  v
AKS Kubernetes cluster
  |
  v
Helm + Argo CD deployment
```

## Why this is separate

The project intentionally starts local-first so the DevOps workflow can be practiced without cloud cost or cloud account setup.

Terraform can be added later as an infrastructure extension.
