# Learning Roadmap

This roadmap tracks the practical learning path used in the TenderOps Lab project.

## Completed

- Created a GitHub-backed repository.
- Added a project skeleton.
- Built a Spring Boot API.
- Added Maven Wrapper.
- Added automated test execution.
- Built a Docker image.
- Ran the API and PostgreSQL with Docker Compose.
- Added GitHub Actions CI.
- Created a local Kubernetes cluster with kind.
- Deployed raw Kubernetes manifests.
- Converted the deployment to a Helm chart.
- Installed Argo CD.
- Configured Argo CD to sync the Helm chart from Git.
- Added Actuator health and metrics.
- Added local Trivy security scanning.
- Remediated dependency, image, and Kubernetes hardening findings.
- Added project walkthrough and public-release documentation.
- Added Cursor security-agent workflows.
- Executed a full security posture review and updated security documentation.
- Added CI security gates with Trivy and Helm lint (`.github/workflows/security.yml`).
- Added GHCR image publishing.
- Added runtime Kubernetes Secrets outside Helm values.
- Added a PostgreSQL NetworkPolicy.
- Added a local Prometheus/Grafana stack with ServiceMonitor scraping.

## Planned

- Promote GHCR images into kind by digest or public pull.
- Add Loki, alerting, and tracing.
- Add a lightweight Terraform/Azure example as a later optional segment.
- Add external secret management beyond local runtime Secrets.
