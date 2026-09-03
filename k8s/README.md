# Kubernetes

Raw YAML used before Helm. The active path is:

```text
Helm chart → Argo CD → Kubernetes
```

These files are a **learning subset**, not a drop-in equivalent of [charts/tenderops](../charts/README.md). Helm additionally renders a PostgreSQL NetworkPolicy, a ServiceMonitor, a Grafana dashboard ConfigMap, and PostgreSQL container resource requests/limits.

## Contents

- [base/namespace.yaml](base/namespace.yaml) — namespace `tenderops`
- [base/api.yaml](base/api.yaml) — ConfigMap, Deployment, Service; references `tenderops-api-runtime-secret`
- [base/postgres.yaml](base/postgres.yaml) — PVC, Deployment, Service; references `tenderops-db-runtime-secret`

There are no `kind: Secret` objects here. Create the runtime Secrets first, as in [charts/README.md](../charts/README.md).

API and PostgreSQL Pods still include the same basic container hardening as the chart (non-root, dropped capabilities, no privilege escalation, read-only root filesystem, seccomp `RuntimeDefault`). The API also has resource requests and limits.

## Manual apply (learning only)

```bash
kubectl apply -f k8s/base/namespace.yaml
kubectl apply -f k8s/base/postgres.yaml
kubectl apply -f k8s/base/api.yaml
```

For normal project use, see [gitops/apps/README.md](../gitops/apps/README.md).
