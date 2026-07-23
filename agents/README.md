# Agents

This directory is reserved for Cursor agent instructions and agentic development workflows.

Agents are not required for the current interview demo. They are a follow-up area for experimenting with security remediation workflows.

## Planned use cases

Cursor agents may be used to help with:

- reading Trivy findings
- identifying affected files
- proposing safe remediation steps
- patching dependencies or Kubernetes manifests
- running tests and scans
- summarizing residual risk
- preparing reviewable change notes

## Safety expectations

Agents should not:

- commit secrets
- push directly without review
- delete unrelated files
- suppress findings without justification
- change architecture without explanation
- modify areas explicitly marked as out of scope

## Related documentation

See:

```text
docs/security-agent-roadmap.md
```
