# Demo Commands

This page is a public command transcript for TenderOps Lab.

It lists commands and **expected result descriptions**. It does not paste live cluster or scanner output. Re-run the commands on your machine to capture current results.

Optional screenshots can be added later under `docs/screenshots/`. This repository does not ship binary screenshot files.

## Prerequisites

Install and verify:

- Docker and Docker Compose
- kind
- kubectl
- Helm 3
- jq (for formatted JSON and Prometheus target tables)
- Trivy (for local security scans)
- Java 21 only if you run Maven directly; the API image build and Maven Wrapper do not require a global Maven install

Useful version checks:

```bash
docker version
docker compose version
kind version
kubectl version --client
helm version
trivy --version
```

Expected result: each command prints a client version without connection errors.

Clone the repository and work from the repository root unless a command says otherwise.

## Docker Compose path

Start the local stack:

```bash
docker compose up --build -d
```

Expected result: `tenderops-api` and `tenderops-db` are running. Compose maps API `8080:8080` and PostgreSQL `5433:5432`.

Health and API checks:

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/api/tenders | jq
curl http://localhost:8080/api/tenders/summary
```

Expected result:

- health reports the application is up
- `/api/tenders` returns JSON tenders
- `/api/tenders/summary` returns `service`, `tenderCount`, and `status`

Local CI equivalent (Maven tests + image build):

```bash
./scripts/ci/api-check.sh
```

Expected result: tests pass and `tenderops-api:0.1.0` is built.

Stop Compose when finished:

```bash
docker compose down
```

Expected result: the Compose containers are removed. The named volume `tenderops-db-data` remains unless you add `-v`.

## Kubernetes path

These commands assume a kind cluster named `tenderops`. Adjust the name if your cluster is different.

### Cluster bootstrap

Create the cluster if it does not exist:

```bash
kind create cluster --name tenderops
kubectl cluster-info
```

Expected result: kubectl talks to the kind control plane.

Install the local monitoring stack first so Prometheus Operator CRDs (including ServiceMonitor) exist before Argo CD syncs the chart:

```bash
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update
helm upgrade --install monitoring prometheus-community/kube-prometheus-stack \
  --namespace monitoring \
  --create-namespace \
  -f observability/kube-prometheus-stack-values.yaml
```

Expected result: Prometheus and Grafana Pods become Ready in namespace `monitoring`. Grafana values in this repo are local-only (`admin` / `admin`).

Install Argo CD:

```bash
kubectl create namespace argocd
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
```

Expected result: Argo CD Pods become Ready in namespace `argocd`. Wait until they are Ready before applying the TenderOps Application.

### Runtime Secrets and image

```bash
kubectl create namespace tenderops

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

Expected result: two Secrets exist in `tenderops`. These values are local demo credentials only.

Build and load the API image used by the Helm defaults:

```bash
docker build -t tenderops-api:0.1.0 ./src/api
kind load docker-image tenderops-api:0.1.0 --name tenderops
```

Expected result: kind reports that the image was loaded into cluster `tenderops`.

After later API, Dockerfile, or dependency changes, reload and restart:

```bash
docker build -t tenderops-api:0.1.0 ./src/api
kind load docker-image tenderops-api:0.1.0 --name tenderops
kubectl rollout restart deployment/tenderops-api -n tenderops
kubectl rollout status deployment/tenderops-api -n tenderops
```

Expected result: the rollout completes and the new Pods become Ready.

### Deploy through GitOps

```bash
kubectl apply -f gitops/apps/tenderops-application.yaml
kubectl get applications -n argocd
kubectl get pods -n tenderops
kubectl get deployment tenderops-api -n tenderops
```

Expected result: Application `tenderops` becomes Synced and Healthy. API replicas and PostgreSQL are Running. See [gitops/apps/README.md](../gitops/apps/README.md).

Port-forward the API:

```bash
kubectl port-forward -n tenderops svc/tenderops-api 8080:8080
```

Expected result: `localhost:8080` reaches the ClusterIP Service. Keep this terminal open while testing.

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/api/tenders | jq
```

Helm validation without changing the cluster:

```bash
helm lint charts/tenderops
helm template tenderops charts/tenderops --namespace tenderops
```

Expected result: lint succeeds. Default render does not include Helm-created Secret objects; workloads reference `tenderops-api-runtime-secret` and `tenderops-db-runtime-secret`.

## Argo CD checks

```bash
kubectl get applications -n argocd
kubectl describe application tenderops -n argocd
```

Expected result:

```text
NAME        SYNC STATUS   HEALTH STATUS
tenderops   Synced        Healthy
```

If the GitHub repository is private, Argo CD needs a read-only repository credential. If the repository is public, that credential should be removed. See [docs/public-release-checklist.md](public-release-checklist.md).

A GitOps demo: change `api.replicas` in `charts/tenderops/values.yaml`, commit, and push to `main`. Argo CD should reconcile the live Deployment. Do not do that as part of a read-only walkthrough.

## Security checks

```bash
./scripts/security/scan-local.sh
```

Expected result: the script writes reports under `reports/security/` and exits 0 even when findings exist. CI (`.github/workflows/security.yml`) is the HIGH/CRITICAL gate.

Optional summaries after a local scan:

```bash
grep -n "Total:" reports/security/trivy-fs.txt reports/security/trivy-image.txt
grep -n "Failures:" reports/security/trivy-k8s-config.txt
```

Expected result: each report contains a totals or failures section. Do not treat a local `--exit-code 0` scan as a production approval.

## Observability checks

API Actuator (with the API port-forward running):

```bash
curl http://localhost:8080/actuator
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/metrics
curl http://localhost:8080/actuator/prometheus | head -20
```

Expected result: health is UP, metrics JSON is present, and Prometheus text metrics include JVM and HTTP series.

Workload inspection:

```bash
kubectl get pods -n tenderops
kubectl logs -n tenderops -l app=tenderops-api --tail=100
kubectl get events -n tenderops --sort-by=.metadata.creationTimestamp
kubectl get servicemonitor -A
```

Expected result: API and database Pods are Ready. A ServiceMonitor for `tenderops-api` exists in namespace `tenderops`.

## Prometheus target check

```bash
kubectl port-forward -n monitoring svc/monitoring-kube-prometheus-prometheus 9090:9090
```

Expected result: Prometheus listens on `localhost:9090`.

```bash
curl -s http://localhost:9090/api/v1/targets \
  | jq -r '
    .data.activeTargets[]
    | [.health, .labels.namespace, .labels.service, .labels.job, .scrapeUrl, .lastError]
    | @tsv
  ' | sort
```

Expected result: a target for namespace `tenderops`, service `tenderops-api`, health `up`, empty `lastError`. Exact job names depend on the kube-prometheus-stack defaults.

Optional PromQL check:

```bash
curl -s --get http://localhost:9090/api/v1/query \
  --data-urlencode 'query=up{namespace="tenderops", service="tenderops-api"}' | jq
```

Expected result: value `1` when the target is healthy. Generate traffic first if HTTP rate panels look empty:

```bash
for i in {1..20}; do
  curl -s http://localhost:8080/api/tenders > /dev/null
  curl -s http://localhost:8080/actuator/health > /dev/null
done
```

More queries: [docs/observability.md](observability.md).

## Grafana dashboard check

```bash
kubectl port-forward -n monitoring svc/monitoring-grafana 3000:80
```

Expected result: Grafana is reachable at `http://localhost:3000`.

Local-only login:

```text
username: admin
password: admin
```

Expected result: the sidecar loads the `TenderOps API` dashboard from the chart ConfigMap labeled `grafana_dashboard=1`. Confirm scrape status and HTTP/JVM panels after traffic has been generated.

Do not reuse these Grafana credentials outside the local lab.

## Cleanup commands

Compose:

```bash
docker compose down
```

Optional: also remove the Compose database volume with `docker compose down -v`.

GitOps application only (leaves kind, Argo CD, and monitoring in place):

```bash
kubectl delete -f gitops/apps/tenderops-application.yaml
```

Expected result: Argo CD prunes or deletes the Application according to the manifest delete. Confirm with `kubectl get applications -n argocd`.

Destroy the kind cluster (destructive for this local cluster):

```bash
kind delete cluster --name tenderops
```

Expected result: cluster `tenderops` is removed. Do not run this unless you intend to rebuild the lab cluster.

## Screenshots

Add optional PNG/JPEG captures later under:

```text
docs/screenshots/
```

Suggested captures, if you add them manually:

- Argo CD Application Synced / Healthy
- `kubectl get pods -n tenderops`
- Prometheus targets showing `tenderops-api` up
- Grafana TenderOps API dashboard
- GitHub Actions security workflow success

Do not commit kubeconfigs, tokens, or screenshots that show secret values.
