# ADR 0002: Use Helm and Argo CD for Kubernetes delivery

## Status

Accepted

## Context

The project originally used raw Kubernetes manifests applied manually with `kubectl apply`. This is useful for learning, but it does not fully demonstrate reusable deployment packaging or GitOps-style delivery.

## Decision

Use Helm to package the Kubernetes deployment and Argo CD to synchronize the Helm chart from GitHub into the local kind cluster.

## Consequences

Positive:

- Deployment configuration is reusable and parameterized.
- Git becomes the source of truth for desired Kubernetes state.
- Argo CD can detect and correct drift.
- The project demonstrates a realistic Kubernetes delivery workflow.

Tradeoffs:

- More moving parts than raw manifests.
- Argo CD needs repository credentials for private GitHub repositories.
- Local kind image loading is still required until a container registry is introduced.
