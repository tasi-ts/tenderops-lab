# Cursor Agents

Launcher examples for the Cursor security workflows. Purpose, file list, and safety expectations: [agents/README.md](../README.md). Runbooks and prompt files live in this folder.

## Runbooks and prompts

- [security-remediation-agent.md](security-remediation-agent.md)
- [security-remediation-prompts.md](security-remediation-prompts.md)
- [security-posture-review-agent.md](security-posture-review-agent.md)
- [security-posture-review-prompts.md](security-posture-review-prompts.md)

Cursor rules: `.cursor/rules/tenderops-project.mdc`, `security-remediation-agent.mdc`, `security-posture-review-agent.mdc`.

## Example launchers

Remediation verification:

```text
Read agents/cursor/security-remediation-prompts.md and execute “Prompt 3: Clean scan verification”.

Use the repo Cursor rules.

Do not edit files unless an actual finding requires remediation.
```

Posture review:

```text
Read agents/cursor/security-posture-review-prompts.md and execute “Prompt 1: Full security posture review”.

Use the repo Cursor rules.

Do not edit files.
```

Validation commands and hard restrictions are in the runbooks and Cursor rules. Do not commit, push, or expose secrets from these workflows.
