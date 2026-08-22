# Cursor Agents

This directory contains Cursor-specific agent workflow documentation for TenderOps Lab.

The current focus is a security remediation workflow that helps process Trivy findings and turn them into controlled, reviewable changes.

## Implemented runbooks

- `agents/cursor/security-remediation-agent.md`

This runbook defines how a Cursor agent should:

- read Trivy reports
- classify findings
- identify affected files
- propose minimal remediation
- patch files
- run validation commands
- summarize fixed and remaining risk

## Cursor project rules

The repo also contains Cursor project rules in `.cursor/rules/`.

Current rules:

- `.cursor/rules/tenderops-project.mdc`
- `.cursor/rules/security-remediation-agent.mdc`

These rules define project boundaries, safety constraints, validation commands, and security remediation behavior.

## Recommended Cursor workflow

1. Open this repository in Cursor.
2. Make sure the latest Trivy reports exist by running `./scripts/security/scan-local.sh`.
3. Open Cursor Agent mode.
4. Use the prompt from `agents/cursor/security-remediation-agent.md`.
5. Let the agent analyze findings and propose small patches.
6. Review the diff manually.
7. Run validation commands.
8. Commit only after human review.

## Important guardrails

Cursor agents should not:

- commit automatically
- push automatically
- expose secret values
- delete unrelated files
- suppress findings without justification
- make broad architecture changes without explanation

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
- reusable security remediation runbook

Planned:

- test the security remediation workflow on a real future finding
- add additional runbooks for documentation updates or Kubernetes hardening
