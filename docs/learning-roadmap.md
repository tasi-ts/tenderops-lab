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
- Added interview demo documentation.
- Added Cursor security-agent workflows.
- Executed a full security posture review and updated security documentation.
- Added CI security gates with Trivy and Helm lint (`.github/workflows/security.yml`).

## Planned

- Add NetworkPolicies and stronger secret handling.
- Add Prometheus/Grafana or another monitoring stack.
- Add a lightweight Terraform/Azure example.
- Publish images to a registry instead of loading them directly into kind.
