# Cursor Agents

This directory is reserved for Cursor-specific agent instructions.

The current interview demo does not depend on Cursor agents. This area is intended for follow-up experimentation with agent-assisted DevSecOps workflows.

## Planned agent types

Possible future agents:

- security finding triage agent
- dependency remediation agent
- Kubernetes hardening agent
- documentation update agent
- test-and-rescan agent

## Intended workflow

```text
Scanner output
  |
  v
Cursor agent reviews findings
  |
  v
Agent proposes controlled changes
  |
  v
Human reviews the diff
  |
  v
Tests and scans are rerun
  |
  v
Changes are committed
```

## Guardrails

Cursor agents should follow these rules:

- stay within the current repository
- avoid unrelated refactoring
- do not touch secrets or credentials
- do not push changes automatically
- explain every proposed change
- run tests after code changes
- rerun scans after security changes
- summarize residual findings honestly

## Current status

Not implemented yet.

This folder documents the intended location for future Cursor agent instruction files.
