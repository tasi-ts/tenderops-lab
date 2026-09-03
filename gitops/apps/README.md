# Argo CD Applications

This directory contains Argo CD `Application` manifests. Parent notes: [gitops/README.md](../README.md).

## Current application

```text
tenderops-application.yaml
```

This manifest tells Argo CD:

- which Git repository to watch
- which branch to use
- which path contains the Helm chart
- which Kubernetes cluster to deploy into
- which namespace to deploy into
- whether automated sync is enabled
- whether pruning and self-healing are enabled

## Current deployment target

```text
Repository: https://github.com/tasi-ts/tenderops-lab.git
Branch: main
Chart path: charts/tenderops
Namespace: tenderops
```

## Check application status

```bash
kubectl get applications -n argocd
```

Expected result:

```text
NAME        SYNC STATUS   HEALTH STATUS
tenderops   Synced        Healthy
```

## Useful troubleshooting command

```bash
kubectl describe application tenderops -n argocd
```

This shows sync errors, repository access problems, rendering errors, and health information.

Full bootstrap and GitOps demo commands: [docs/demo-commands.md](../../docs/demo-commands.md). Runtime Secrets: [charts/README.md](../../charts/README.md).
