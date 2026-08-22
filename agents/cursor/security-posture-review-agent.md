# Security Posture Review Agent Runbook

This runbook defines a Cursor workflow for reviewing the broader security posture of TenderOps Lab.

The remediation agent focuses on concrete scanner findings. This posture review agent looks beyond scanner output and evaluates architecture, configuration, operations, and production-readiness gaps.

## When to use this runbook

Use this runbook when asking Cursor to review:

- overall security posture
- DevSecOps maturity
- Kubernetes hardening gaps
- CI/CD security gaps
- observability and operational readiness
- production-readiness limitations
- local-lab versus production differences

## Main objective

Produce a structured security posture review that clearly separates:

- implemented strengths
- acceptable local-lab limitations
- production gaps
- recommended next improvements
- suggested priorities

A clean Trivy scan is positive evidence, but it does not prove production readiness.

## Suggested input files

The agent should inspect relevant files from:

```text
README.md
docs/architecture.md
docs/project-goals.md
docs/security-hardening.md
docs/observability.md
docs/interview-prep-summary.md
scripts/security/scan-local.sh
.github/workflows/
src/api/pom.xml
src/api/Dockerfile
charts/tenderops/values.yaml
charts/tenderops/templates/
k8s/base/
gitops/apps/
infra/
observability/
```

If fresh scan reports exist, the agent may also inspect:

```text
reports/security/trivy-fs.txt
reports/security/trivy-image.txt
reports/security/trivy-k8s-config.txt
```

## Review categories

Classify observations as one of:

- current strength
- local-lab acceptable limitation
- production gap
- security risk
- operational risk
- recommended next improvement

## Review checklist

### Application security

Review:

- API surface
- input validation
- error handling
- dependency hygiene
- database migration behavior
- Actuator exposure

### Container security

Review:

- base image choice
- image tag strategy
- non-root execution
- read-only root filesystem
- Linux capabilities
- privilege escalation settings
- writable filesystem mounts

### Kubernetes security

Review:

- namespace separation
- Secret and ConfigMap usage
- resource requests and limits
- readiness and liveness probes
- pod security context
- container security context
- NetworkPolicies
- service exposure
- persistent volume use

### GitOps and CI/CD security

Review:

- Argo CD repository access model
- GitHub token scope assumptions
- private repo access
- manual versus automated deployment boundaries
- CI testing
- CI security scanning
- security gate behavior
- image registry and image promotion gaps

### Observability and operations

Review:

- health endpoints
- metrics endpoints
- logs
- Kubernetes events
- alerting gaps
- dashboard gaps
- incident troubleshooting readiness

### Infrastructure security

Review:

- local-only assumptions
- Terraform/Azure readiness
- external secret management gaps
- managed database gaps
- registry gaps
- monitoring/logging gaps

## Output format

The agent should produce this structure:

```text
Security posture review

Current strengths:
- ...

Local-lab acceptable limitations:
- ...

Production gaps:
- ...

Security risks:
- ...

Operational risks:
- ...

Recommended next improvements:
1. ...
2. ...
3. ...

Suggested priority:
- short term:
- medium term:
- later:

Files or docs that should be updated:
- ...
```

## Rules

The agent should not edit files during the first review pass.

The first pass should be analysis-only.

After the user approves, the agent may help create follow-up tasks or documentation updates.

## Hard restrictions

Do not:

- expose secrets or credentials
- ask the user to paste tokens
- modify files without approval
- claim production readiness without caveats
- invent implemented controls that are not present
- treat clean scanner output as complete security approval
- recommend expensive cloud work before simpler local controls unless justified

## Example launcher prompt

Use this prompt in Cursor Agent mode:

```text
Read agents/cursor/security-posture-review-agent.md and execute a full security posture review.

Use the repo Cursor rules.

Do not edit files.

Inspect the current project structure, documentation, Helm chart, Kubernetes manifests, security scan workflow, CI workflow, Dockerfile, and GitOps configuration.

Produce a structured review with:
1. current strengths
2. local-lab acceptable limitations
3. production gaps
4. security risks
5. operational risks
6. recommended next improvements
7. suggested priority
8. files or docs that should be updated

Do not claim production readiness. Clearly distinguish local-lab scope from production requirements.
```
