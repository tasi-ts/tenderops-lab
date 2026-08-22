# Security Remediation Agent Runbook

This runbook defines a reusable Cursor workflow for analyzing and remediating security findings in TenderOps Lab.

The goal is not to let an agent make uncontrolled changes. The goal is to use the agent as a structured DevSecOps assistant that can inspect findings, propose fixes, apply small patches, run validation commands, and summarize residual risk.

## When to use this runbook

Use this runbook when working on:

- Trivy filesystem findings
- Maven dependency vulnerabilities
- Docker image vulnerabilities
- Kubernetes or Helm misconfigurations
- secret scan findings
- container hardening
- security documentation updates

## Expected input files

The agent should inspect these files when available:

```text
reports/security/trivy-fs.txt
reports/security/trivy-image.txt
reports/security/trivy-k8s-config.txt
```

Relevant source/configuration files:

```text
src/api/pom.xml
src/api/Dockerfile
charts/tenderops/values.yaml
charts/tenderops/templates/api.yaml
charts/tenderops/templates/postgres.yaml
k8s/base/api.yaml
k8s/base/postgres.yaml
scripts/security/scan-local.sh
docs/security-hardening.md
```

## Agent objective

The agent should follow this loop:

```text
Read scan reports
  |
  v
Classify findings
  |
  v
Identify affected files
  |
  v
Propose minimal fixes
  |
  v
Apply small patches
  |
  v
Run validation
  |
  v
Summarize fixed and remaining risk
```

## Finding classification

Classify every finding into one of these categories:

- Maven dependency vulnerability
- application dependency vulnerability inside image
- Docker base image vulnerability
- Kubernetes misconfiguration
- Helm template misconfiguration
- raw Kubernetes manifest misconfiguration
- possible secret exposure
- false positive
- accepted residual risk

## Remediation guidance

### Maven dependency vulnerability

Preferred approach:

1. Identify the vulnerable dependency.
2. Check the fixed version reported by Trivy.
3. Update only the affected dependency/version property.
4. Run Maven tests.

Validation command:

```bash
cd src/api && ./mvnw clean test && cd ../..
```

Do not upgrade unrelated dependencies unless required.

### Docker image vulnerability

Preferred approach:

1. Determine whether the finding comes from:
   - the application JAR
   - the base image OS packages
   - a binary included in the base image
2. If it comes from the application JAR, fix the application dependency first.
3. If it comes from the base image, consider a minimal base image change.
4. Rebuild the image.
5. Re-scan the image.

Validation commands:

```bash
docker build -t tenderops-api:0.1.0 ./src/api
trivy image --severity HIGH,CRITICAL --ignore-unfixed tenderops-api:0.1.0
```

### Kubernetes or Helm misconfiguration

Preferred approach:

1. Patch the active Helm chart first.
2. Keep raw manifests in `k8s/base/` consistent when practical.
3. Prefer explicit pod and container security contexts.
4. Validate with Helm and Trivy.

Useful hardening settings:

```yaml
securityContext:
  runAsNonRoot: true
  seccompProfile:
    type: RuntimeDefault
```

Container-level example:

```yaml
securityContext:
  allowPrivilegeEscalation: false
  readOnlyRootFilesystem: true
  capabilities:
    drop:
      - ALL
```

Validation commands:

```bash
helm lint charts/tenderops
helm template tenderops charts/tenderops --namespace tenderops
./scripts/security/scan-local.sh
```

### Secret findings

Preferred approach:

1. Do not print secret values.
2. Determine whether the finding is a real credential, test value, generated value, or false positive.
3. If real, remove it from Git-tracked files.
4. Rotate the secret outside the repository.
5. Document the remediation without exposing the value.

Do not commit tokens, kubeconfigs, private keys, passwords, or cloud credentials.

## Required validation levels

Use the smallest relevant validation first.

For dependency changes:

```bash
cd src/api && ./mvnw clean test && cd ../..
```

For Dockerfile changes:

```bash
docker build -t tenderops-api:0.1.0 ./src/api
```

For Helm chart changes:

```bash
helm lint charts/tenderops
helm template tenderops charts/tenderops --namespace tenderops
```

For full security validation:

```bash
./scripts/security/scan-local.sh
```

For GitOps/Kubernetes validation:

```bash
kubectl get applications -n argocd
kubectl get pods -n tenderops
kubectl get deployment tenderops-api -n tenderops
```

## Agent output format

At the end of a run, the agent should produce a summary like this:

```text
Security remediation summary

Findings reviewed:
- ...

Files changed:
- ...

Validation commands run:
- ...

Findings fixed:
- ...

Findings remaining:
- ...

Residual risk:
- ...

Recommended next action:
- ...
```

## Human review requirements

The agent may propose and apply local changes, but a human should review before:

- committing
- pushing
- suppressing findings
- deleting files
- changing architecture
- changing security policy
- rotating or modifying credentials

## Hard restrictions

The agent must not:

- commit automatically
- push automatically
- hide findings by deleting reports
- add broad ignore rules without justification
- weaken security settings just to make tests pass
- expose secret values in summaries
- touch unrelated files
- perform destructive commands without explicit approval

## Example prompt for Cursor

Use this prompt inside Cursor Agent mode:

```text
Use the TenderOps Lab project rules and the Security Remediation Agent rule.

Analyze the current Trivy reports under reports/security/.

Classify each finding by type, identify the source files responsible, and propose minimal remediation steps.

Patch only the files needed to remediate the findings. For active deployment configuration, patch the Helm chart first and keep k8s/base manifests consistent where practical.

After changes, run the smallest relevant validation commands:
- Maven tests for dependency changes
- Docker build for image changes
- helm lint and helm template for chart changes
- ./scripts/security/scan-local.sh for full security validation

Do not commit or push. Do not suppress findings without justification. Do not expose secret values.

At the end, summarize:
1. findings reviewed
2. files changed
3. validation commands run
4. findings fixed
5. findings remaining
6. residual risk
7. recommended next action
```

## Current project-specific notes

The active deployment path is:

```text
Helm chart → Argo CD → Kubernetes
```

The raw manifests in `k8s/base/` are retained for learning/reference purposes.

Generated scan reports live under:

```text
reports/security/
```

These reports are local generated artifacts and are ignored by Git.
