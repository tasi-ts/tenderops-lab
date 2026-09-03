# Security Posture Review Agent Prompts

This file contains reusable Cursor Agent prompts for broader security posture review work.

Use these prompts together with:

- `.cursor/rules/tenderops-project.mdc`
- `.cursor/rules/security-posture-review-agent.mdc`
- `agents/cursor/security-posture-review-agent.md`

## Prompt 1: Full security posture review

Use this when you want a broad analysis of the current project security posture.

Prompt:

Read `agents/cursor/security-posture-review-agent.md` and execute a full security posture review.

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

Do not claim production readiness.

Clearly distinguish:

- what is already implemented
- what is acceptable for a local lab
- what would be needed for production
- what is optional portfolio enhancement

## Prompt 2: Production-readiness gap review

Use this when you specifically want to compare the local lab against a production-style deployment.

Prompt:

Read `agents/cursor/security-posture-review-agent.md`.

Perform a production-readiness security gap review for TenderOps Lab.

Do not edit files.

Focus on the difference between the current local-first implementation and a production deployment.

Evaluate:

1. secrets management
2. Kubernetes hardening
3. network isolation
4. CI/CD security gates
5. image registry and image promotion
6. observability and alerting
7. database backup/restore
8. cloud infrastructure assumptions
9. GitOps access and permissions
10. operational incident readiness

For each gap, classify it as:

- acceptable for local lab
- recommended portfolio improvement
- required for production

End with a prioritized action plan.

## Prompt 3: Portfolio improvement review

Use this when you want the agent to suggest next GitHub portfolio improvements.

Prompt:

Read `agents/cursor/security-posture-review-agent.md`.

Review TenderOps Lab as a GitHub portfolio project.

Do not edit files.

Focus on what would make the project more credible, understandable, and useful for public project review.

Evaluate:

1. README clarity
2. documentation structure
3. reproducibility
4. security story
5. observability story
6. cloud/Terraform extension potential
7. CI/CD maturity
8. demo readiness
9. agentic workflow story

Suggest a prioritized roadmap with:

- quick wins
- medium-sized improvements
- larger optional extensions

Clearly mark anything that is optional or not yet implemented.

## Prompt 4: Security posture documentation update

Use this only after a posture review has been completed and the user approves documentation changes.

Prompt:

Read the latest security posture review result and update documentation accordingly.

Use the repo Cursor rules.

Edit only documentation files.

Do not change application code, Helm templates, Kubernetes manifests, CI workflows, or scripts.

Update relevant files such as:

- `docs/security-hardening.md`
- `docs/project-review-summary.md`
- `agents/README.md`

Keep `docs/security-agent-roadmap.md` as a short pointer. Do not restore a second copy of the agent status lists.

Clearly document:

1. implemented security controls
2. local-lab limitations
3. production gaps
4. recommended next improvements
5. accepted residual risks, if any

Do not claim production readiness.

Do not invent controls that are not implemented.
