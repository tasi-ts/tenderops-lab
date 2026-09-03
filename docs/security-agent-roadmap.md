# Security Agent Roadmap

This document captures the Cursor-based security-agent workflows for TenderOps Lab.

The goal is to use Cursor agents as controlled DevSecOps assistants, not as fully autonomous actors.

## Current status

Implemented:

- project-wide Cursor rule
- security remediation Cursor rule
- security posture review Cursor rule
- reusable remediation runbook
- reusable remediation prompts
- reusable posture review runbook
- reusable posture review prompts
- agent documentation in `agents/`
- Cursor-specific documentation in `agents/cursor/`
- full security posture review executed in Cursor
- posture review documentation update
- CI security gates in `.github/workflows/security.yml`

Relevant files:

- `.cursor/rules/tenderops-project.mdc`
- `.cursor/rules/security-remediation-agent.mdc`
- `.cursor/rules/security-posture-review-agent.mdc`
- `agents/README.md`
- `agents/cursor/README.md`
- `agents/cursor/security-remediation-agent.md`
- `agents/cursor/security-remediation-prompts.md`
- `agents/cursor/security-posture-review-agent.md`
- `agents/cursor/security-posture-review-prompts.md`
- `.github/workflows/security.yml`
- `docs/security-hardening.md`

## Security Remediation Agent

Purpose:

The Security Remediation Agent is used when scanner findings exist and need controlled remediation.

Typical inputs:

- `reports/security/trivy-fs.txt`
- `reports/security/trivy-image.txt`
- `reports/security/trivy-k8s-config.txt`

Typical tasks:

- classify Trivy findings
- identify affected files
- propose minimal remediation
- patch dependencies, Dockerfile, Helm templates, or Kubernetes manifests after approval
- run validation commands
- summarize fixed findings, remaining findings, and residual risk

Expected workflow:

1. Run `./scripts/security/scan-local.sh`.
2. Ask the remediation agent to analyze the reports.
3. Review the agent analysis.
4. Approve remediation only if the analysis is correct.
5. Let the agent apply minimal patches.
6. Run tests and scans again.
7. Commit only after human review.

## Security Posture Review Agent

Purpose:

The Security Posture Review Agent is used when scans may be clean but the broader security posture still needs review.

Typical tasks:

- review local-lab security controls
- distinguish local-lab limitations from production gaps
- assess Kubernetes hardening maturity
- assess CI/CD security maturity
- assess observability and incident-readiness gaps
- assess secrets and infrastructure assumptions
- recommend prioritized next improvements

Expected workflow:

1. Ask the posture review agent for an analysis-only review.
2. Review strengths, gaps, risks, and recommended improvements.
3. Decide which recommendations to implement.
4. Create follow-up implementation tasks separately.
5. After approval, use Prompt 4 to update documentation only.

### Latest posture review outcome

The first full posture review confirmed:

- strong local hardening baseline (non-root, capabilities drop, read-only root filesystem, seccomp, probes, API resource limits)
- useful local Trivy workflow and agent runbooks
- CI security gates now implemented via `.github/workflows/security.yml`
- remaining gaps that are acceptable for local lab but required for production: external secrets, broader NetworkPolicies, registry promotion, auth, backups, alerting

Accepted residual risks for the lab are documented in `docs/security-hardening.md`.

## Finding categories

The remediation agent should classify findings as:

- Maven dependency vulnerability
- application dependency vulnerability inside image
- Docker base image vulnerability
- Kubernetes misconfiguration
- Helm template misconfiguration
- raw Kubernetes manifest misconfiguration
- possible secret exposure
- false positive
- accepted residual risk

The posture review agent should classify observations as:

- current strength
- local-lab acceptable limitation
- production gap
- security risk
- operational risk
- recommended next improvement

## Validation commands

The agents should choose the smallest relevant validation command first.

For Java dependency changes:

- `cd src/api && ./mvnw clean test && cd ../..`

For Docker image changes:

- `docker build -t tenderops-api:0.1.0 ./src/api`

For Helm chart changes:

- `helm lint charts/tenderops`
- `helm template tenderops charts/tenderops --namespace tenderops`

For full security validation:

- `./scripts/security/scan-local.sh`

For Kubernetes/GitOps validation:

- `kubectl get applications -n argocd`
- `kubectl get pods -n tenderops`
- `kubectl get deployment tenderops-api -n tenderops`

## Safety boundaries

Agents must not:

- commit automatically
- push automatically
- expose secrets or credentials
- delete unrelated files
- suppress findings without justification
- weaken security settings just to make tests pass
- change architecture without explanation
- run destructive commands without explicit human approval
- claim production readiness without caveats

## Human review requirement

Agents may propose and apply local edits after approval, but a human should review before:

- committing
- pushing
- suppressing findings
- changing security posture
- changing deployment architecture
- rotating or modifying credentials

## Next steps

Planned future improvements:

- add NetworkPolicies and PostgreSQL resource limits
- move secrets out of committed values
- add image registry promotion
- keep accepted residual risk documentation current after remediation cycles
- add additional agent runbooks only when repeated workflows emerge
