# TenderOps Lab — Project Review Summary

Technical Q&A and talking points. Pair with [docs/project-walkthrough.md](project-walkthrough.md) and [docs/demo-commands.md](demo-commands.md). One-minute pitch and production gaps: [README.md](../README.md). Status of what was built: [docs/learning-roadmap.md](learning-roadmap.md).

## 1. What the application does

The API stores tender records in PostgreSQL. A simple health/demo check:

```bash
curl http://localhost:8080/api/tenders/summary
```

Expected response:

```json
{"service":"tenderops-api","tenderCount":2,"status":"running"}
```

The application is not a full tender-management product. It is a small service used to demonstrate infrastructure, deployment, security, and observability.

## 2. Why these technologies

Short “why it is in the lab,” not a second copy of the architecture or CI docs.

| Technology | Role in the lab |
| --- | --- |
| Java 21 / Spring Boot | Typical JVM API; Actuator feeds probes and Prometheus |
| Maven Wrapper (`./mvnw`) | Same build locally and in CI without a global Maven install |
| PostgreSQL + Flyway | Real database dependency; schema versioned in Git |
| Docker / Compose | Repeatable image; fastest local API + database loop |
| GitHub Actions | Tests, Trivy HIGH/CRITICAL gates, GHCR publish; CI does not deploy |
| kind | Kubernetes without cloud cost; works with `kind load` |
| Helm | Parameterized packaging of the Kubernetes objects |
| Argo CD | Git is desired state; cluster is reconciled from `main` |
| Actuator / Prometheus / Grafana | Probes, scrape via ServiceMonitor, local dashboard |
| Trivy | Advisory local scan vs enforcing CI gates |

Kubernetes objects in the **active** Helm path include Namespace, runtime Secrets, ConfigMap, Deployment, Service, PVC, NetworkPolicy, ServiceMonitor, probes, resources, and securityContext. Raw YAML in `k8s/base/` is a smaller learning subset; see [k8s/README.md](../k8s/README.md).

CI, GHCR tags, and the deploy boundary: [docs/ci-cd.md](ci-cd.md). Delivery diagrams: [docs/architecture.md](architecture.md).

## 3. Useful clarifying Q&A

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

Those NetworkPolicy and ServiceMonitor objects come from the Helm chart, not from `k8s/base/`.

### Why are they called manifests?

They declaratively state the desired Kubernetes resources. They are not scripts that say “do this step-by-step.” They describe the desired end state.

### What does `kubectl apply` mean?

```text
Kubernetes, make the real cluster match what this YAML describes.
```

Normal lab delivery uses Argo CD to apply the Helm chart from Git, not a standing `kubectl apply` of `k8s/base/`.

### Why does `kubectl get all -n tenderops` show more than two things?

Kubernetes lists resources, not applications. Each component may have:

```text
Deployment → manages rollout
ReplicaSet → maintains replica count
Pod → actual running container instance
Service → stable network endpoint
```

So `tenderops-api` and `tenderops-db` become multiple objects.

### What is a Pod?

The smallest runnable unit in Kubernetes.

```text
tenderops-api Pod → Spring Boot API container
tenderops-db Pod  → PostgreSQL container
```

The API currently runs two replicas. PostgreSQL runs one Pod.

### Why use Helm after raw YAML?

Raw YAML is useful for learning. Helm packages and parameterizes the same kind of objects (`replicas`, image tag, resources, security settings) through `values.yaml`.

### What is Argo CD?

A GitOps continuous delivery tool for Kubernetes. It watches a Git repository and synchronizes the cluster to match the desired state stored in Git.

### What does GitOps mean here?

Git is the source of truth. Instead of routinely running `kubectl apply`, `helm install`, or `helm upgrade`, the preferred flow is:

```text
Edit chart/config → commit and push → Argo CD detects change → Argo CD syncs Kubernetes
```

### Why did Argo CD initially fail to read the repo?

The GitHub repo was private. The local Git client had credentials; Argo CD inside Kubernetes did not. That was fixed with a limited GitHub token stored as Argo CD repository credentials.

If the repository is public, Argo CD should clone over HTTPS without that token. Remove the old credential and rotate the token. See [docs/public-release-checklist.md](public-release-checklist.md).

### What permissions were needed for the GitHub token?

Fine-grained token, while the repo was private:

```text
Repository: only tasi-ts/tenderops-lab
Contents: read-only
Metadata: read-only
Expiration: short-lived
```

The token should not have write or admin permissions.

### What did the GitOps demo prove?

Changing `api.replicas` in Helm values and pushing to Git caused Argo CD to reconcile the live Deployment:

```text
Git change → push → Argo CD sync → Kubernetes state changes
```

### What did `./mvnw clean test` do?

The project Maven Wrapper ran a clean build and tests: delete `target/`, resolve dependencies, compile, run tests, report success or failure.

### Why rerun scans after fixes?

Remediation is not complete until verified:

```text
scan → fix → rebuild/redeploy → rescan → compare results
```

### Does CI deploy to Kubernetes?

No. GitHub Actions tests, scans, and can publish to GHCR. Argo CD deploys from Git.

### Does kind pull from GHCR today?

Not in the default lab path. Helm still uses `tenderops-api:0.1.0` with `IfNotPresent`. After image changes, `kind load` is required unless GHCR is public or an imagePullSecret exists.

## 4. What to emphasize in a technical review

Main message: the project is a small Spring Boot API with a DevOps lifecycle around it, not a complex business domain.

Strong talking points:

- Docker Compose versus Kubernetes
- Raw YAML versus Helm
- GitOps and Argo CD reconciliation
- Health / readiness / liveness
- Scan → remediate → rescan, and local advisory scan versus CI gates
- Runtime Secrets versus committed Helm passwords
- ServiceMonitor scrape versus curling Actuator by hand
- What is still required before production (including optional later Terraform/Azure)

If asked why the app is simple:

```text
The app is intentionally small because the purpose of the project is to demonstrate the platform and delivery workflow. A complex business domain would distract from the DevOps concepts being practiced.
```

If asked what would improve next:

```text
Next steps include promoting images by digest from GHCR into kind, adding external secret management, Loki and alerting, and optionally a Terraform/Azure infrastructure example. CI security gates with Trivy are already in place.
```

If asked about production readiness:

```text
This is not production-ready. It is a local lab that demonstrates the workflow. Container hardening, a database NetworkPolicy, Prometheus/Grafana, GHCR publish, and CI Trivy gates are in place, but production would still need proper secret management, registry-based image promotion, environment-specific values, Ingress/TLS, API auth, backup/restore, and stronger alerting.
```

Suggested live order: [docs/project-walkthrough.md](project-walkthrough.md). Commands: [docs/demo-commands.md](demo-commands.md). Hardening detail: [docs/security-hardening.md](security-hardening.md). Observability: [docs/observability.md](observability.md).
