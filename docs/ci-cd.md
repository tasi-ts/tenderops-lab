# CI/CD

TenderOps Lab starts with a simple continuous integration workflow.

## Current CI checks

The GitHub Actions workflow runs on pushes to `main` and on pull requests.

It currently performs:

- Java setup with Temurin 21
- Maven test execution for the API service
- Docker image build for the API service

## Local equivalent

Run the local CI helper script from the repository root:

```bash
./scripts/ci/api-check.sh
```

## Planned additions

- Dependency scanning
- Container image scanning
- GitLab CI example
- Jenkins pipeline example
- Deployment dry-runs
- Helm chart validation
- Kubernetes manifest validation
