# Security Agent Roadmap

Cursor security-agent workflows for TenderOps Lab. Agents are controlled assistants, not autonomous actors.

**Index:** [agents/README.md](../agents/README.md)  
**Cursor launchers:** [agents/cursor/README.md](../agents/cursor/README.md)  
**Residual risk and hardening:** [docs/security-hardening.md](security-hardening.md)  
**CI gates:** [docs/ci-cd.md](ci-cd.md)

## Still open

- Additional agent runbooks only when a repeated workflow emerges
- Refresh residual-risk docs after future hardening cycles
- Image promotion by digest into kind (platform work, not an agent runbook)

NetworkPolicy, PostgreSQL resource limits, and runtime Secrets outside Helm values are already implemented.
