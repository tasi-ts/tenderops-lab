# Architecture

TenderOps Lab demonstrates a small application moving through a modern DevOps delivery path.

## Application architecture

```text
Client / curl
  |
  v
tenderops-api
  |
  v
PostgreSQL
```

The API is a Java/Spring Boot service. PostgreSQL stores tender records.

## Local development architecture

```text
Developer machine
  |
  +-- Maven build/test
  |
  +-- Docker image build
  |
  +-- Docker Compose
        |
        +-- API container
        +-- PostgreSQL container
```

Docker Compose is used for local multi-container development.

## Kubernetes architecture

```text
kind cluster
  |
  +-- namespace: tenderops
        |
        +-- tenderops-api Deployment
        |     +-- 2 API Pods
        |     +-- ClusterIP Service
        |
        +-- tenderops-db Deployment
              +-- 1 PostgreSQL Pod
              +-- ClusterIP Service
              +-- PersistentVolumeClaim
```

## Delivery architecture

```text
GitHub repository
  |
  +-- GitHub Actions
  |     +-- Maven tests
  |     +-- Docker build
  |     +-- Trivy security gates + Helm lint
  |
  +-- Argo CD
        +-- watches Helm chart in Git
        +-- syncs desired state into Kubernetes
```

## Security and observability

```text
Spring Boot Actuator
  |
  +-- health
  +-- readiness/liveness
  +-- metrics

Trivy
  |
  +-- filesystem/dependency scan
  +-- secret scan
  +-- Docker image scan
  +-- Kubernetes/Helm misconfiguration scan
  +-- CI security gates in GitHub Actions
```
