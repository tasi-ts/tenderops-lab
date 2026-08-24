# Helm Charts

This directory contains the Helm chart used to deploy TenderOps Lab.

## Chart

```text
charts/tenderops
```

The chart deploys:

- Spring Boot API
- PostgreSQL database
- Kubernetes Secrets
- Kubernetes ConfigMaps
- Kubernetes Services
- PersistentVolumeClaim for PostgreSQL
- readiness and liveness probes
- resource requests and limits
- basic container security contexts

## Useful commands

Render the chart locally:

```bash
helm template tenderops charts/tenderops --namespace tenderops
```

Validate the chart:

```bash
helm lint charts/tenderops
```

Install manually:

```bash
helm install tenderops charts/tenderops --namespace tenderops --create-namespace
```

Upgrade manually:

```bash
helm upgrade tenderops charts/tenderops --namespace tenderops
```

## Current role

In the current project, Argo CD manages this Helm chart from Git.

The preferred deployment flow is:

```text
Change Helm chart or values
  |
  v
Commit and push to GitHub
  |
  v
Argo CD syncs the change into Kubernetes
```

## Runtime secrets

The TenderOps Helm chart does not commit runtime database passwords in `values.yaml`.

By default, the chart expects these Kubernetes Secrets to already exist:

- `tenderops-api-runtime-secret`
- `tenderops-db-runtime-secret`

Create them in the local kind namespace before syncing the Argo CD application:

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

These are local demo credentials only.

Production credentials should be provided by a real secret-management workflow, such as External Secrets, SOPS, Sealed Secrets, Vault, Azure Key Vault, or CI/CD-controlled secret injection.

## Values files

The chart uses:

- `values.yaml` for common defaults
- `values-prod-example.yaml` as a production-shaped example
- `values-local.yaml` only as an optional local override file

`values-local.yaml` is gitignored because local override files may contain demo credentials.

## Secret rendering behavior

The chart supports two secret modes:

```yaml
secrets:
  create: false
```

This is the default mode. Helm does not render Kubernetes Secret objects. The workload references existing runtime Secrets.

```yaml
secrets:
  create: true
```

This mode may be used only for controlled local experiments. If enabled, a password value must be supplied externally and should not be committed to Git.

## Validation

Render the chart with default values:

```bash
helm lint charts/tenderops

helm template tenderops charts/tenderops \
  --namespace tenderops
```

Check that no secret values are rendered:

```bash
helm template tenderops charts/tenderops \
  --namespace tenderops > /tmp/tenderops.yaml

grep -n "kind: Secret\|POSTGRES_PASSWORD\|SPRING_DATASOURCE_PASSWORD" /tmp/tenderops.yaml
```

Expected default behavior:

- no Helm-rendered `kind: Secret` objects
- no literal database password in rendered manifests
- Deployments reference the runtime Secret names