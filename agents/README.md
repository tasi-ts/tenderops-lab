# Agents

This directory contains agent workflow documentation for TenderOps Lab.

The current implemented agent workflow is focused on security remediation with Cursor.

## Current purpose

The agent workflow is designed to help process security findings in a controlled way.

It should help with:

- reading Trivy reports
- classifying findings
- identifying affected files
- proposing minimal remediation steps
- patching dependencies or Kubernetes manifests
- running tests and scans
- summarizing residual risk
- preparing reviewable change notes

## Implemented workflow

The current Cursor-specific runbook is:

- `agents/cursor/security-remediation-agent.md`

Supporting Cursor project rules are stored in:

- `.cursor/rules/tenderops-project.mdc`
- `.cursor/rules/security-remediation-agent.mdc`

## Safety expectations

Agents should not:

- commit automatically
- push automatically
- expose secrets or credentials
- delete unrelated files
- suppress findings without justification
- change architecture without explanation
- modify areas explicitly marked as out of scope

## Recommended usage model

The intended workflow is:

1. Run security scans.
2. Ask the agent to analyze the scan reports.
3. Let the agent propose small, focused fixes.
4. Review the diff manually.
5. Run tests and scans again.
6. Commit only after human review.

## Current status

Implemented:

- project-wide Cursor rule
- security remediation Cursor rule
- reusable security remediation runbook
- Cursor agent README

Planned:

- test the agent workflow on future security findings
- add additional runbooks for documentation updates
- add additional runbooks for Kubernetes hardening
- explore CI integration for security validation
