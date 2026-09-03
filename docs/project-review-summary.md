# TenderOps Lab — Project Review Summary

Technical review notes for TenderOps Lab. Pair this with [docs/project-walkthrough.md](project-walkthrough.md) and [docs/demo-commands.md](demo-commands.md).

## 1. One-minute project summary

TenderOps Lab is a local-first DevOps portfolio project built to demonstrate a software delivery workflow around a small Java/Spring Boot API.

The application is intentionally simple: a tender-management API backed by PostgreSQL. The main value is the delivery pipeline around it:

```text
Spring Boot API
→ Maven build/test
→ Docker image
→ Docker Compose
→ Kubernetes
→ Helm
→ Argo CD GitOps
→ Actuator + Prometheus/Grafana
→ Trivy security scanning
```

The project demonstrates build automation, containerization, Kubernetes deployment, GitOps, observability, and DevSecOps scanning. It is portfolio-ready, not production-ready.

## 2. What the application does

The API exposes tender-related endpoints and stores data in PostgreSQL.

A simple health/demo endpoint is:

```bash
curl http://localhost:8080/api/tenders/summary
```

Expected response:

```json
{"service":"tenderops-api","tenderCount":2,"status":"running"}
```

The application is not a full tender-management product. It is a small service used to demonstrate infrastructure, deployment, security, and observability practices.

## 3. Main technologies used

### Java 21

Used as the application runtime language/platform.

Why it matters:

- common enterprise backend technology
- fits well with Spring Boot
- relevant to DevOps work around JVM services

### Spring Boot

Used to build the REST API.

Why it matters:

- provides a fast way to build Java APIs
- includes embedded web server support
- integrates with health checks, metrics, configuration, and database access
- Actuator gives operational endpoints used by probes and Prometheus

### Maven and Maven Wrapper

Used for build and test automation.

Command:

```bash
./mvnw clean test
```

What it does:

```text
./mvnw  → runs the project-bundled Maven Wrapper
clean   → deletes old build output from target/
test    → compiles code and runs automated tests
```

Why it matters:

- repeatable build process
- dependency management
- local and CI environments can run the same build command

### PostgreSQL

Used as the application database.

Why it matters:

- realistic relational database dependency
- demonstrates application-to-database configuration
- used both in Docker Compose and Kubernetes

### Flyway

Used for database migrations.

Why it matters:

- database schema is versioned in code
- application startup can initialize required database structure
- avoids manual database setup

### Docker

Used to package the API into a container image.

Why it matters:

- application runs consistently across environments
- runtime dependencies are packaged with the app
- image can be used by Docker Compose and Kubernetes

### Docker Compose

Used for local multi-container development.

It runs:

- API container
- PostgreSQL container

Why it matters:

- simple local environment
- useful before moving to Kubernetes
- demonstrates service-to-service configuration

### GitHub Actions

Used for CI, security gates, and image publishing.

Current functional CI (`.github/workflows/ci.yml`) validates:

- Maven tests
- Docker image build

Current security workflow (`.github/workflows/security.yml`) validates:

- Helm lint
- Trivy filesystem scan
- Trivy image scan
- Trivy rendered Kubernetes config scan
- report artifact upload
- fail on HIGH/CRITICAL findings

Current publish workflow (`.github/workflows/image-publish.yml`) publishes:

```text
ghcr.io/tasi-ts/tenderops-api:main
ghcr.io/tasi-ts/tenderops-api:sha-<commit>
```

Why it matters:

- automated verification on push/PR
- CI security gates, not only local scanning
- registry publish is separate from cluster deploy

### kind

Used to run a local Kubernetes cluster inside Docker.

Why it matters:

- allows Kubernetes practice without cloud cost
- works well with locally built Docker images
- useful for fast demos and learning

### Kubernetes

Used to run the API and PostgreSQL as cluster workloads.

Important Kubernetes objects used:

- Namespace
- Secret
- ConfigMap
- Deployment
- ReplicaSet
- Pod
- Service
- PersistentVolumeClaim
- NetworkPolicy
- ServiceMonitor
- readiness probe
- liveness probe
- resource requests and limits
- securityContext

Why it matters:

- demonstrates container orchestration
- shows how apps are deployed, exposed, scaled, and monitored in Kubernetes

### Helm

Used to package Kubernetes YAML into a reusable chart.

Why it matters:

- avoids duplicated raw YAML
- makes configuration parameterized through `values.yaml`
- closer to real Kubernetes delivery practice

### Argo CD

Used for GitOps-style continuous delivery.

Why it matters:

- Git becomes the source of truth
- Argo CD watches the GitHub repo
- Argo CD compares desired state with live cluster state
- Argo CD automatically syncs changes into Kubernetes

### Spring Boot Actuator, Prometheus, and Grafana

Actuator provides health and metrics. Prometheus scrapes `/actuator/prometheus` through a ServiceMonitor. Grafana is installed via kube-prometheus-stack for local dashboards.

Important API endpoints:

```bash
curl http://localhost:8080/actuator
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/metrics
curl http://localhost:8080/actuator/prometheus | head -20
```

Why it matters:

- application-level health visibility
- readiness/liveness support
- metrics that Prometheus actually scrapes in this lab

### Trivy

Used for local and CI security scanning.

Current scan workflow covers:

- filesystem/dependency scanning
- secret scanning
- Docker image vulnerability scanning
- Helm-rendered Kubernetes misconfiguration scanning
- CI enforcement of HIGH/CRITICAL findings via `.github/workflows/security.yml`

Why it matters:

- demonstrates DevSecOps awareness
- turns vulnerabilities/misconfigurations into actionable remediation work
- shows the difference between local advisory scans and CI security gates

## 4. Delivery flow

The current delivery flow is:

```text
Developer changes code or config
  |
  v
Commit and push to GitHub
  |
  v
GitHub Actions runs CI checks and security gates
  |
  v
Argo CD reads desired state from Git
  |
  v
Helm chart is rendered
  |
  v
Kubernetes cluster is reconciled
  |
  v
API and PostgreSQL run in the tenderops namespace
```

Important clarification:

The active deployment path is:

```text
Helm chart → Argo CD → Kubernetes
```

The raw Kubernetes manifests are kept as learning/reference material, but normal deployment should happen through Git and Argo CD.

## 5. Code execution flow

At runtime:

```text
HTTP request
  |
  v
Spring Boot Controller
  |
  v
Service layer
  |
  v
Repository / JDBC
  |
  v
PostgreSQL
```

In Kubernetes:

```text
curl / browser
  |
  v
kubectl port-forward
  |
  v
tenderops-api Service
  |
  v
one of the tenderops-api Pods
  |
  v
tenderops-db Service
  |
  v
PostgreSQL Pod
  |
  v
PersistentVolumeClaim-backed storage
```

The API currently runs with 2 replicas. PostgreSQL runs as 1 Pod.

## 6. Important commands to know

### Build and test

```bash
cd src/api
./mvnw clean test
cd ../..
```

### Local CI check

```bash
./scripts/ci/api-check.sh
```

### Docker build

```bash
docker build -t tenderops-api:0.1.0 ./src/api
```

### Load image into kind

```bash
kind load docker-image tenderops-api:0.1.0 --name tenderops
```

### Kubernetes checks

```bash
kubectl get nodes
kubectl get pods -n tenderops
kubectl get all -n tenderops
kubectl get deployment tenderops-api -n tenderops
```

### Argo CD checks

```bash
kubectl get applications -n argocd
kubectl describe application tenderops -n argocd
```

Expected Argo CD status:

```text
tenderops   Synced   Healthy
```

### Port-forward API

```bash
kubectl port-forward -n tenderops service/tenderops-api 8080:8080
```

### Test API

```bash
curl http://localhost:8080/api/tenders/summary
```

### Test observability

```bash
curl http://localhost:8080/actuator
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/metrics
```

### Run security scan

```bash
./scripts/security/scan-local.sh
```

### Check scan summaries

```bash
grep -n "Total:" reports/security/trivy-fs.txt reports/security/trivy-image.txt
grep -n "Failures:" reports/security/trivy-k8s-config.txt
```

## 7. Useful clarifying Q&A

### What is Kubernetes defining with the manifests?

The PostgreSQL workload defines:

```text
Secret → database name/user/password (runtime Secret)
PVC → persistent storage
Deployment → PostgreSQL container workload
Service → stable internal database address
NetworkPolicy → API Pods only on TCP 5432
```

The API workload defines:

```text
Secret → database credentials (runtime Secret)
ConfigMap → non-secret app configuration
Deployment → API Pods, probes, resources, security context
Service → stable internal API address
ServiceMonitor → Prometheus scrape of /actuator/prometheus
```

### Why are they called manifests?

Because they declaratively state the desired Kubernetes resources.

They are not scripts saying “do this step-by-step.” They describe the desired end state.

### What does `kubectl apply` mean?

It means:

```text
Kubernetes, make the real cluster match what this YAML describes.
```

### Why does `kubectl get all -n tenderops` show more than two things?

Because Kubernetes shows resources, not just applications.

For each logical component, Kubernetes may have:

```text
Deployment → manages rollout
ReplicaSet → maintains replica count
Pod → actual running container instance
Service → stable network endpoint
```

So `tenderops-api` and `tenderops-db` become multiple Kubernetes resources.

### What is a Pod?

A Pod is the smallest runnable unit in Kubernetes.

In this project:

```text
tenderops-api Pod → runs the Spring Boot API container
tenderops-db Pod  → runs the PostgreSQL container
```

### Why use Helm after raw YAML?

Raw YAML is useful for learning.

Helm is useful because it packages and parameterizes Kubernetes deployment configuration.

Example:

```text
replicas: 2
image tag: 0.1.0
database name: tenderops
resource limits
security settings
```

These can be controlled through `values.yaml`.

### What is Argo CD?

Argo CD is a GitOps continuous delivery tool for Kubernetes.

It watches a Git repository and synchronizes the cluster to match the desired state stored in Git.

### What does GitOps mean here?

Git is the source of truth.

Instead of manually running:

```bash
kubectl apply
helm install
helm upgrade
```

the preferred flow is:

```text
Edit chart/config
  |
  v
Commit and push
  |
  v
Argo CD detects change
  |
  v
Argo CD syncs Kubernetes
```

### Why did Argo CD initially fail to read the repo?

Because the GitHub repo was private.

The local Git client had credentials, but Argo CD inside Kubernetes did not.

This was fixed by creating a limited GitHub token and adding it to Argo CD as repository credentials.

If the repository is made public, Argo CD should clone over HTTPS without that token. The old credential should then be removed and the token rotated. See [docs/public-release-checklist.md](public-release-checklist.md).

### What permissions were needed for the GitHub token?

For this lab, a fine-grained token with limited access was enough while the repo was private:

```text
Repository: only tasi-ts/tenderops-lab
Contents: read-only
Metadata: read-only
Expiration: short-lived
```

The token should not have write/admin permissions.

### What did the GitOps demo prove?

Changing `api.replicas` in the Helm values file and pushing to Git caused Argo CD to reconcile the cluster.

That proved:

```text
Git change → push → Argo CD sync → Kubernetes state changes
```

### What did `./mvnw clean test` do?

It used the project Maven Wrapper to run a clean build and tests:

```text
delete old target/ output
resolve dependencies
compile main code
compile test code
run automated tests
report success/failure
```

### Why rerun scans after fixes?

Because remediation is not complete until verified.

The correct loop is:

```text
scan → fix → rebuild/redeploy → rescan → compare results
```

### Does CI deploy to Kubernetes?

No. GitHub Actions tests, scans, and can publish to GHCR. Argo CD deploys from Git.

### Does kind pull from GHCR today?

Not in the default lab path. Helm still uses `tenderops-api:0.1.0` with `IfNotPresent`. After image changes, `kind load` is required unless GHCR is public or an imagePullSecret exists.

## 8. Security hardening summary

The project added local Trivy scanning, remediated findings, ran a broader security posture review, and added CI security gates.

Examples of security work performed:

- upgraded vulnerable PostgreSQL JDBC dependency
- adjusted Docker runtime image
- ran the API as a non-root user
- added Kubernetes pod/container security contexts
- dropped Linux capabilities
- disabled privilege escalation
- enabled read-only root filesystem
- added temporary writable volumes where needed
- added seccomp `RuntimeDefault`
- moved runtime database passwords out of committed Helm values
- added a PostgreSQL NetworkPolicy
- kept generated reports out of Git
- added `.github/workflows/security.yml` with Trivy fs/image/config scans, Helm lint, and artifact upload
- documented local-lab limitations, production gaps, and accepted residual risks

Security context examples included:

```text
runAsNonRoot: true
allowPrivilegeEscalation: false
readOnlyRootFilesystem: true
capabilities.drop: ALL
seccompProfile: RuntimeDefault
```

Important distinction:

- implemented: local hardening, NetworkPolicy for PostgreSQL, CI security gates, GHCR publish workflow
- acceptable for local lab: demo credentials, no API auth, local image load, Actuator detail on the app port
- still needed for production: external secrets, Ingress/TLS, registry promotion by digest, backups, alerting, API auth, cloud secret management

## 9. Observability summary

Implemented:

- Actuator health, liveness, and readiness
- Actuator JSON metrics
- Actuator Prometheus endpoint
- Kubernetes readiness and liveness probes
- kube-prometheus-stack in namespace `monitoring`
- ServiceMonitor for `tenderops-api`
- Grafana dashboard ConfigMap for the API
- `kubectl logs`, `kubectl describe`, and `kubectl get events`

Not a production monitoring platform yet. Missing pieces include Loki, Alertmanager rules, tracing, authenticated Grafana, and retention/alerting design.

## 10. What to emphasize in a technical review

### Main message

This project is not about building a complex application. It is about demonstrating the delivery lifecycle around a service.

```text
TenderOps Lab is a small Spring Boot API with a DevOps lifecycle around it: Maven build/test, Docker packaging, Docker Compose, Kubernetes, Helm, Argo CD GitOps, Actuator and Prometheus/Grafana observability, and Trivy security scanning.
```

### Strong talking points

- Difference between Docker Compose and Kubernetes
- Raw Kubernetes YAML versus Helm
- GitOps and Argo CD reconciliation
- Why health/readiness/liveness checks matter
- Why scanning must be followed by remediation and rescan
- Dependency, image, and Kubernetes misconfiguration findings
- Runtime Secrets versus committed Helm passwords
- ServiceMonitor-based scrape versus curling Actuator by hand
- What would still be required before production, including an optional later Terraform/Azure path

### If asked why the app is simple

```text
The app is intentionally small because the purpose of the project is to demonstrate the platform and delivery workflow. A complex business domain would distract from the DevOps concepts being practiced.
```

### If asked what would improve next

```text
Next steps include promoting images by digest from GHCR into kind, adding external secret management, Loki and alerting, and optionally a Terraform/Azure infrastructure example. CI security gates with Trivy are already in place.
```

### If asked about production readiness

```text
This is not production-ready. It is a local lab that demonstrates the workflow. Container hardening, a database NetworkPolicy, Prometheus/Grafana, GHCR publish, and CI Trivy gates are in place, but production would still need proper secret management, registry-based image promotion, environment-specific values, Ingress/TLS, API auth, backup/restore, and stronger alerting.
```

## 11. Current project status

Completed:

- GitHub repository
- Spring Boot API
- Maven build/test
- Docker image
- Docker Compose with PostgreSQL
- GitHub Actions CI
- GitHub Actions security gates (Trivy + Helm lint)
- GHCR image publish workflow
- local Kubernetes with kind
- raw Kubernetes manifests
- Helm chart
- Argo CD GitOps
- private-repo access pattern for Argo CD (until the repo is public)
- GitOps replica-change demo
- Actuator health, metrics, and Prometheus scrape
- local Prometheus/Grafana stack
- Trivy security scanning
- security remediation/hardening
- runtime Kubernetes Secrets
- PostgreSQL NetworkPolicy
- Cursor security-agent workflows
- security posture review and documentation update
- project walkthrough and public-release checklist

Planned / optional later:

- GHCR public pull and digest promotion into kind
- Loki, alerting, and tracing
- external secret management
- API authentication
- Terraform/Azure extension

## 12. Suggested demo order

Recommended order:

```text
1. Open README and explain the project in one minute.
2. Show the Spring Boot API structure briefly.
3. Run or show CI script / GitHub Actions.
4. Show Kubernetes resources.
5. Show Argo CD Synced/Healthy.
6. Hit the API endpoint.
7. Show Actuator, Prometheus targets, and Grafana.
8. Show Trivy scan script and explain remediation versus CI gates.
9. End with local-only assumptions and production gaps.
```

Useful command sequence:

```bash
kubectl get applications -n argocd
kubectl get pods -n tenderops
kubectl get deployment tenderops-api -n tenderops
curl http://localhost:8080/api/tenders/summary
curl http://localhost:8080/actuator/metrics
./scripts/security/scan-local.sh
```

## 13. Concise project pitch

```text
TenderOps Lab is a local DevOps delivery lab for a Spring Boot API. It uses Maven for build/test, Docker for packaging, Docker Compose for local multi-container development, kind for local Kubernetes, Helm for deployment packaging, Argo CD for GitOps delivery, Actuator plus Prometheus/Grafana for observability, and Trivy for security scanning. The project shows the path from code to container to Kubernetes, including GitOps, observability, and hardening, without claiming production readiness.
```
