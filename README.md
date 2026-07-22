# TenderOps Lab

A local-first DevOps and security automation lab for practicing Docker, Kubernetes, CI/CD, GitOps, observability, Terraform, and Cursor-based security agents.

## Purpose

This repository simulates a small B2B tendering-platform environment. The application is intentionally simple; the main learning goal is the DevOps, operations, and security workflow around it.

## Planned capabilities

- Local development with Docker and Docker Compose
- Containerized API and worker services
- Local database
- CI/CD pipeline examples
- Kubernetes deployment manifests
- Helm packaging
- GitOps deployment with ArgoCD
- Observability with metrics, logs, and dashboards
- Terraform infrastructure examples
- Security review workflows and Cursor agent instructions

## Status

Initial repository skeleton.

## Local CI check

Run:

```bash
./scripts/ci/api-check.sh
```

This runs API tests and builds the API docker image.
