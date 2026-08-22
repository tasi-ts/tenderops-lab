# TenderOps Lab — Interview Preparation Summary

## 1. One-minute project summary

TenderOps Lab is a local-first DevOps portfolio project built to demonstrate a modern software delivery workflow around a small Java/Spring Boot API.

The application itself is intentionally simple: a tender-management API backed by PostgreSQL. The main value of the project is the delivery pipeline around it:

```text
Spring Boot API
→ Maven build/test
→ Docker image
→ Docker Compose
→ Kubernetes
→ Helm
→ Argo CD GitOps
→ Actuator observability
→ Trivy security scanning
```

The project demonstrates practical knowledge of build automation, containerization, Kubernetes deployment, GitOps, basic observability, and DevSecOps scanning.

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

The application is not intended to be a full tender-management product. It is a small service used to demonstrate infrastructure, deployment, and security practices.

## 3. Main technologies used

### Java 21

Used as the application runtime language/platform.

Why it matters:

- common enterprise backend technology
- fits well with Spring Boot
- relevant to many DevOps roles involving Java services

### Spring Boot

Used to build the REST API.

Why it matters:

- provides a fast way to build Java APIs
- includes embedded web server support
- integrates well with health checks, metrics, configuration, and database access
- Actuator gives production-style operational endpoints

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

Used for CI and security gates.

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

Why it matters:

- automated verification on push/PR
- demonstrates basic continuous integration
- demonstrates CI security gates, not only local scanning
- reduces reliance on manual local checks

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

### Spring Boot Actuator

Used for health and metrics endpoints.

Important endpoints:

```bash
curl http://localhost:8080/actuator
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/metrics
```

Why it matters:

- application-level health visibility
- readiness/liveness support
- runtime metrics that could later be scraped by Prometheus

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
GitHub Actions runs CI checks
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

The active deployment path is now:

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

The PostgreSQL manifest defines the database resources:

```text
Secret → database name/user/password
PVC → persistent storage
Deployment → PostgreSQL container workload
Service → stable internal database address
```

The API manifest defines the application resources:

```text
Secret → database credentials
ConfigMap → non-secret app configuration
Deployment → API Pods, probes, resources, security context
Service → stable internal API address
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

### What permissions were needed for the GitHub token?

For this lab, a fine-grained token with limited access was enough:

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

- implemented: local hardening + CI security gates
- acceptable for local lab: demo credentials, no auth, no NetworkPolicies, local images
- still needed for production: external secrets, network isolation, registry promotion, backups, alerting, API auth

## 9. Observability summary

The project currently uses lightweight observability.

Implemented:

- Actuator health endpoint
- Actuator readiness/liveness endpoints
- Actuator metrics endpoint
- Kubernetes readiness probe
- Kubernetes liveness probe
- `kubectl logs`
- `kubectl describe`
- `kubectl get events`

This is not a full monitoring stack yet, but it gives a practical base.

Possible future extension:

```text
Prometheus → scrape metrics
Grafana    → dashboards
Loki       → centralized logs
Alerting   → operational notifications
```

## 10. What to emphasize in the interview

### Main message

This project is not about building a complex application. It is about demonstrating the delivery lifecycle around a service.

Say something like:

```text
I built a small Spring Boot API and focused on the DevOps lifecycle around it: Maven build/test, Docker packaging, Docker Compose, Kubernetes, Helm, Argo CD GitOps, Actuator observability, and Trivy security scanning.
```

### Strong talking points

- You can explain the difference between Docker Compose and Kubernetes.
- You can explain raw Kubernetes YAML versus Helm.
- You can explain GitOps and Argo CD reconciliation.
- You can explain why health/readiness/liveness checks matter.
- You can explain why scanning must be followed by remediation and rescan.
- You can explain dependency, image, and Kubernetes misconfiguration findings.
- You can explain what would move to Terraform/Azure in a real cloud setup.

### Good answer if asked why the app is simple

```text
The app is intentionally small because the purpose of the project is to demonstrate the platform and delivery workflow. A complex business domain would distract from the DevOps concepts I wanted to practice.
```

### Good answer if asked what you would improve next

```text
Next I would add NetworkPolicies, move secrets out of Git, publish images to a registry, add Prometheus/Grafana, and optionally a small Terraform/Azure version of the infrastructure. CI security gates with Trivy are already in place.
```

### Good answer if asked about production readiness

```text
This is not production-ready yet. It is a local lab that demonstrates the workflow. We already have container hardening and CI Trivy gates, but for production I would still add proper secret management, registry-based image promotion, environment-specific values, NetworkPolicies, monitoring/alerting, backup/restore for PostgreSQL, and cloud infrastructure managed with Terraform.
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
- local Kubernetes with kind
- raw Kubernetes manifests
- Helm chart
- Argo CD GitOps
- private repo access for Argo CD
- GitOps replica-change demo
- Actuator health and metrics
- Trivy security scanning
- security remediation/hardening
- Cursor security-agent workflows
- security posture review and documentation update
- project documentation cleanup
- interview demo guide

Planned:

- NetworkPolicies and stronger secret handling
- optional monitoring stack
- optional Terraform/Azure extension
- optional image registry flow

## 12. Demo order for the interview

Recommended order:

```text
1. Open README and explain the project in one minute.
2. Show the Spring Boot API structure briefly.
3. Run or show CI script / GitHub Actions.
4. Show Kubernetes resources.
5. Show Argo CD Synced/Healthy.
6. Hit the API endpoint.
7. Show Actuator metrics.
8. Show Trivy scan script and explain remediation.
9. End with future improvements.
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

## 13. Final concise pitch

```text
TenderOps Lab is a local DevOps delivery lab for a Spring Boot API. I used Maven for build/test, Docker for packaging, Docker Compose for local multi-container development, kind for local Kubernetes, Helm for deployment packaging, Argo CD for GitOps delivery, Actuator for health and metrics, and Trivy for security scanning. The project helped me practice the end-to-end path from code to container to Kubernetes deployment, including observability and security hardening.
```
