# Security Agent Roadmap

This document captures the planned Cursor-based security-agent workflow.

The security agents are not required for the current interview demo. They are a follow-up exploration area.

## Planned agent responsibilities

The agent should be able to:

- Read Trivy reports.
- Classify findings by type.
- Identify likely remediation files.
- Propose safe patches.
- Apply changes only inside approved areas.
- Run tests and scans.
- Summarize fixed and remaining findings.

## Finding categories

Expected categories:

- Maven dependency vulnerability
- Docker image vulnerability
- Kubernetes misconfiguration
- secret exposure
- false positive or accepted residual risk

## Safety boundaries

The agent should not:

- commit secrets
- push directly without review
- delete unrelated files
- modify `legacy/` if such a folder exists
- suppress findings without explanation
- change architecture without a written rationale

## Target workflow

```text
Trivy report
  |
  v
Agent analysis
  |
  v
Patch proposal
  |
  v
Human review
  |
  v
Test + rescan
  |
  v
Commit
```
