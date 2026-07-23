# Infrastructure

This directory is reserved for infrastructure-as-code examples.

TenderOps Lab is currently local-first and does not require cloud infrastructure.

## Current status

Implemented locally:

- Docker-based development environment
- local Kubernetes cluster with kind
- local PostgreSQL in Kubernetes
- local Docker image loading into kind
- Argo CD running inside the local cluster

Not yet implemented:

- Azure resource group
- Azure Container Registry
- Azure Kubernetes Service
- managed PostgreSQL
- Key Vault
- Log Analytics

## Future direction

Terraform can be added later to demonstrate cloud infrastructure provisioning.

A possible future cloud version would look like this:

- Terraform creates Azure infrastructure.
- GitHub Actions builds and publishes the Docker image.
- Argo CD deploys the Helm chart to AKS.
- Azure-native services provide registry, secrets, database, and monitoring capabilities.

## Current role of this directory

This directory documents planned infrastructure work. The current working demo remains fully local.
