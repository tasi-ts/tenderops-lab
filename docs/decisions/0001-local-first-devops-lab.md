# ADR 0001: Local-first DevOps lab

## Status

Accepted

## Context

The goal of this project is to practice Docker, Kubernetes, CI/CD, GitOps, observability, Terraform, and security-agent workflows in a controlled environment.

## Decision

The project will be local-first. Docker Compose and a local Kubernetes cluster will be the default execution environments. Azure infrastructure may be added later as an optional extension.

## Consequences

- Lower cost and lower setup friction
- Easier repeatability
- Safer experimentation
- Cloud deployment can be added later without blocking the core learning path
