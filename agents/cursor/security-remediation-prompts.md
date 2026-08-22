# Security Remediation Agent Prompts

This file contains reusable Cursor Agent prompts for the TenderOps Lab security workflow.

Use these prompts together with:

- `.cursor/rules/tenderops-project.mdc`
- `.cursor/rules/security-remediation-agent.mdc`
- `agents/cursor/security-remediation-agent.md`

## Prompt 1: Analyze fresh scan reports

Use this first after running:

- `./scripts/security/scan-local.sh`

Prompt:

Use the repo Cursor rules and follow `agents/cursor/security-remediation-agent.md`.

Analyze the fresh Trivy reports under `reports/security/`:

- `reports/security/trivy-fs.txt`
- `reports/security/trivy-image.txt`
- `reports/security/trivy-k8s-config.txt`

Start in analysis-only mode.

Do not edit files yet.

Give me:

1. findings detected
2. severity and category of each finding
3. likely root cause
4. affected source/configuration files
5. whether each finding is in the active deployment path or only in reference material
6. proposed minimal remediation
7. exact validation commands you would run after remediation

Do not commit.
Do not push.
Do not suppress findings.
Do not expose secret values.

## Prompt 2: Apply approved remediation

Use this only after reviewing and approving the agent analysis.

Prompt:

Approved.

Follow `agents/cursor/security-remediation-agent.md` and apply only the minimal remediation needed for the findings you identified.

Patch the active Helm/Argo CD deployment path first.

Keep `k8s/base/` manifests consistent where practical.

After changes, run the relevant validation commands.

Do not commit.
Do not push.
Do not suppress findings.
Do not expose secret values.

At the end, summarize:

1. files changed
2. validation commands run
3. findings fixed
4. findings remaining
5. residual risk
6. recommended next action

## Prompt 3: Clean scan verification

Use this when the latest Trivy reports appear clean and there may be no remediation to perform.

Prompt:

Use the repo Cursor rules and follow `agents/cursor/security-remediation-agent.md`.

Analyze the fresh Trivy reports under `reports/security/`.

If the reports contain no vulnerabilities, no secrets, and no Kubernetes misconfigurations, do not modify source files.

Instead, produce a concise verification summary covering:

1. reports inspected
2. whether dependency findings are clean
3. whether image findings are clean
4. whether Kubernetes/Helm configuration findings are clean
5. validation commands that were already run or should be rerun
6. whether any remediation is currently required
7. recommended next action

Do not commit.
Do not push.
Do not edit files unless you identify an actual finding that requires remediation.

## Prompt 4: Post-remediation verification

Use this after the agent has applied fixes.

Prompt:

Use the repo Cursor rules and follow `agents/cursor/security-remediation-agent.md`.

Review the current diff and the latest Trivy reports.

Confirm whether the remediation was successful.

Check:

- `git diff`
- `reports/security/trivy-fs.txt`
- `reports/security/trivy-image.txt`
- `reports/security/trivy-k8s-config.txt`

Summarize:

1. files changed
2. findings fixed
3. findings remaining
4. validation evidence
5. whether the change is ready for human commit
6. any residual risk

Do not commit.
Do not push.
