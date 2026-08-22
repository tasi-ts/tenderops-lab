# Agents

This directory contains agent workflow documentation for TenderOps Lab.

The current implemented workflows focus on controlled, agent-assisted DevSecOps work with Cursor.

## Implemented agent workflows

### Security Remediation Agent

Files:

- `agents/cursor/security-remediation-agent.md`
- `agents/cursor/security-remediation-prompts.md`
- `.cursor/rules/security-remediation-agent.mdc`

Purpose:

- read Trivy reports
- classify scanner findings
- identify affected files
- propose minimal remediation steps
- patch dependencies or Kubernetes manifests after approval
- run validation commands
- summarize fixed findings, remaining findings, and residual risk

Use this agent when there are concrete security findings to remediate.

### Security Posture Review Agent

Files:

- `agents/cursor/security-posture-review-agent.md`
- `agents/cursor/security-posture-review-prompts.md`
- `.cursor/rules/security-posture-review-agent.mdc`

Purpose:

- review broader security posture
- distinguish local-lab limitations from production gaps
- assess Kubernetes, CI/CD, observability, secrets, and infrastructure maturity
- recommend prioritized next improvements

Use this agent when scans are clean but the project needs a broader security or production-readiness review.

## Shared project rules

Common project rules live in:

- `.cursor/rules/tenderops-project.mdc`

These rules define:

- repository boundaries
- safety expectations
- active deployment path
- validation commands
- documentation update expectations

## Safety expectations

Agents should not:

- commit automatically
- push automatically
- expose secrets or credentials
- delete unrelated files
- suppress findings without justification
- change architecture without explanation
- modify areas explicitly marked as out of scope
- claim production readiness without caveats

## Recommended usage model

For remediation work:

1. Run security scans.
2. Ask the remediation agent to analyze the scan reports.
3. Review the agent analysis.
4. Approve remediation only if the analysis is correct.
5. Let the agent propose or apply minimal patches.
6. Run tests and scans again.
7. Commit only after human review.

For posture review work:

1. Ask the posture review agent for an analysis-only review.
2. Review strengths, gaps, risks, and recommended improvements.
3. Decide which improvements to implement.
4. Create follow-up implementation tasks separately.
5. After approval, use Prompt 4 to update documentation only.

## Current status

Implemented:

- project-wide Cursor rule
- security remediation Cursor rule
- security posture review Cursor rule
- reusable remediation runbook
- reusable remediation prompts
- reusable posture review runbook
- reusable posture review prompts
- Cursor agent README
- full security posture review executed in Cursor
- posture review documentation update
- CI security gates via `.github/workflows/security.yml`

Planned:

- add additional agent runbooks only when repeated workflows emerge
- keep posture and residual-risk docs current after future hardening work
