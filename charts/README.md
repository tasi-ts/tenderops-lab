# Helm Charts

This directory contains the Helm chart that Argo CD deploys for TenderOps Lab.

## Chart

```text
charts/tenderops
```

The chart renders:

- Spring Boot API Deployment, Service, and ConfigMap
- PostgreSQL Deployment, Service, and PersistentVolumeClaim
- readiness and liveness probes, resource requests and limits, container security contexts
- a PostgreSQL NetworkPolicy
- a ServiceMonitor and a Grafana dashboard ConfigMap (when enabled in values)

By default it **references** pre-created runtime Secrets. It does not render Secret objects. See [Runtime secrets](#runtime-secrets).

## Current role

Argo CD syncs this chart from Git. Preferred flow:

```text
Change Helm chart or values
  |
  v
Commit and push to GitHub
  |
  v
Argo CD syncs the change into Kubernetes
```

Application manifest: [gitops/apps/README.md](../gitops/apps/README.md). Do not `helm install` / `helm upgrade` this release during normal GitOps use.

## Runtime secrets

The chart does not commit the database password in `values.yaml`.

Default Secret names that must already exist in namespace `tenderops`:

- `tenderops-api-runtime-secret`
- `tenderops-db-runtime-secret`

Create them before the Argo CD Application can become healthy:

```bash
kubectl create secret generic tenderops-db-runtime-secret \
  -n tenderops \
  --from-literal=POSTGRES_DB=tenderops \
  --from-literal=POSTGRES_USER=tenderops \
  --from-literal=POSTGRES_PASSWORD=tenderops \
  --dry-run=client -o yaml | kubectl apply -f -

kubectl create secret generic tenderops-api-runtime-secret \
  -n tenderops \
  --from-literal=SPRING_DATASOURCE_USERNAME=tenderops \
  --from-literal=SPRING_DATASOURCE_PASSWORD=tenderops \
  --dry-run=client -o yaml | kubectl apply -f -
```

The same commands appear in [docs/demo-commands.md](../docs/demo-commands.md). These are local demo credentials only.

Production credentials should come from External Secrets, SOPS, Sealed Secrets, Vault, Azure Key Vault, or CI/CD-controlled injection.

## Values files

- `values.yaml` — common defaults
- `values-prod-example.yaml` — production-shaped example
- `values-local.yaml` — optional local override (gitignored; may contain demo credentials)

## Secret rendering behavior

```yaml
secrets:
  create: false
```

Default. Helm does not render Kubernetes Secret objects. Workloads reference the runtime Secret names.

```yaml
secrets:
  create: true
```

Controlled local experiments only. If enabled, supply the password externally and do not commit it.

## Validation

```bash
helm lint charts/tenderops
helm template tenderops charts/tenderops --namespace tenderops
```

Check that default render does not create Secrets or inline passwords:

```bash
helm template tenderops charts/tenderops \
  --namespace tenderops \
  | grep -nE 'kind: Secret|POSTGRES_PASSWORD|SPRING_DATASOURCE_PASSWORD' || true
```

Expected default behavior:

- no Helm-rendered `kind: Secret` objects
- no literal database password in rendered manifests
- Deployments reference the runtime Secret names

## Learning / break-glass only

Manual install is not the lab delivery path. If you use it, create the runtime Secrets first:

```bash
helm install tenderops charts/tenderops --namespace tenderops --create-namespace
helm upgrade tenderops charts/tenderops --namespace tenderops
```
