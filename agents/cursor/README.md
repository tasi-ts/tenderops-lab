# Cursor Agents

This directory contains Cursor-specific agent workflow documentation for TenderOps Lab.

The current focus is controlled agent-assisted DevSecOps work.

## Implemented runbooks

- `agents/cursor/security-remediation-agent.md`
- `agents/cursor/security-posture-review-agent.md`

## Reusable prompt files

- `agents/cursor/security-remediation-prompts.md`
- `agents/cursor/security-posture-review-prompts.md`

## Cursor project rules

The repo also contains Cursor project rules in `.cursor/rules/`.

Current rules:

- `.cursor/rules/tenderops-project.mdc`
- `.cursor/rules/security-remediation-agent.mdc`
- `.cursor/rules/security-posture-review-agent.mdc`

These rules define project boundaries, safety constraints, validation commands, remediation behavior, and posture-review behavior.

## Security Remediation Agent

Use this workflow when scanner findings exist and need controlled remediation.

Typical use cases:

- Trivy dependency findings
- Docker image vulnerabilities
- Kubernetes or Helm misconfigurations
- possible secret findings
- container hardening issues

The expected workflow is:

1. Run `./scripts/security/scan-local.sh`.
2. Ask Cursor Agent to analyze the reports.
3. Review the agent analysis.
4. Approve remediation only if the analysis is correct.
5. Let the agent patch minimal files.
6. Rerun tests and scans.
7. Review the diff manually before commit.

## Security Posture Review Agent

Use this workflow when scans may be clean but the broader security posture still needs review.

Typical use cases:

- production-readiness review
- local-lab versus production gap analysis
- Kubernetes hardening maturity review
- CI/CD security maturity review
- observability and incident-readiness review
- portfolio improvement planning

The expected workflow is:

1. Ask Cursor Agent to execute a posture review prompt.
2. Keep the first pass analysis-only.
3. Review strengths, gaps, risks, and recommended improvements.
4. Approve documentation or implementation tasks separately.

## Recommended Cursor usage

Open Cursor Agent mode and launch a task by referencing one of the prompt files.

Example launcher:

```text
Read agents/cursor/security-remediation-prompts.md and execute “Prompt 3: Clean scan verification”.

Use the repo Cursor rules.

Do not edit files unless an actual finding requires remediation.
```

Another example:

```text
Read agents/cursor/security-posture-review-prompts.md and execute “Prompt 1: Full security posture review”.

Use the repo Cursor rules.

Do not edit files.
```

## Important guardrails

Cursor agents should not:

- commit automatically
- push automatically
- expose secret values
- delete unrelated files
- suppress findings without justification
- make broad architecture changes without explanation
- claim production readiness without caveats

## Useful validation commands

- `./scripts/ci/api-check.sh`
- `./scripts/security/scan-local.sh`
- `helm lint charts/tenderops`
- `helm template tenderops charts/tenderops --namespace tenderops`
- `kubectl get applications -n argocd`
- `kubectl get pods -n tenderops`

## Current status

Implemented:

- project-wide Cursor rule
- security remediation Cursor rule
- security posture review Cursor rule
- reusable security remediation runbook
- reusable security remediation prompts
- reusable security posture review runbook
- reusable security posture review prompts
- Prompt 1 full posture review executed in Cursor
- Prompt 4 documentation update for posture review outcomes
- CI security gates in `.github/workflows/security.yml` (Trivy fs/image/config, Helm lint, artifact upload)

Posture review summary lives in:

- `docs/security-hardening.md`
- `docs/security-agent-roadmap.md`
- `docs/ci-cd.md`

Planned:

- add additional runbooks only when a real repeated workflow emerges
- refresh residual-risk documentation after future hardening changes
