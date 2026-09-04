# Public Release Checklist

Manual checklist for making TenderOps Lab visible as a public portfolio repository and tagging `v0.1.0`.

Do not treat this file as an automation script. Do not change GitHub repository visibility, GHCR visibility, or create a GitHub Release from this checklist until you have completed the reviews below.

This project is local-first and portfolio-ready. It is not production-ready.

## Repository safety

- [x] README, architecture, walkthrough, observability, security, and this checklist describe the lab honestly (local-first, not production).
- [x] No kubeconfigs, cloud credentials, private keys, or personal access tokens are tracked in Git.
- [x] Docker Compose demo credentials in `compose.yaml` are marked local-only.
- [x] Grafana `admin` / `admin` in `observability/kube-prometheus-stack-values.yaml` is marked local-only.
- [x] Helm `values.yaml` does not contain a database password; runtime Secrets are created outside Git.
- [x] `.gitignore` covers `.env`, `secrets/`, `*.secret.yaml`, `reports/security/`, and `charts/tenderops/values-local.yaml`.
- [x] Generated Trivy reports are not committed.

## Git history review

Tracked files can be clean while an old commit still contains a secret.

- [x] Review `git log --oneline --all` for accidental credential commits.
- [x] Search history if there is any doubt a token was ever committed.
- [x] If a real token, password, or key was ever committed: **rotate it immediately**, then clean history with a dedicated process (`git filter-repo` or equivalent) before the repository is public.
- [x] Do not rely on a later commit that merely deletes a secret. History remains readable after the repo is public.

This checklist does not run history rewriting. History cleanup is a separate, explicit operator task.

## Current secret scan

Current tracked-file review (excluding `reports/**`) is expected to find documentation and local-demo references only, for example:

- `${{ secrets.GITHUB_TOKEN }}` in GitHub Actions (automatic token; value is not in Git)
- Compose and Grafana local-only demo passwords
- Helm/Kubernetes Secret **names** and env keys such as `POSTGRES_PASSWORD`
- security agent rules telling agents not to expose credentials
- Maven Wrapper `MVNW_PASSWORD` support in `src/api/mvnw`

Re-run before going public:

```bash
git grep -nEi '(password|secret|token|apikey|api_key|client_secret|private key|begin rsa|begin openssh|ghp_|github_pat_)' \
  -- ':!reports/**' ':!.git/**' || true
```

- [x] Re-run the scan on the release commit.
- [x] Confirm remaining hits are local-demo, documentation, or workflow references.
- [x] Stop and rotate if any `ghp_`, `github_pat_`, private key block, or other live credential appears.

GitHub Actions secret **values** are not committed. Secret **names** and `${{ secrets.* }}` references in workflows are visible in a public repo. That is expected.

## GitHub repository visibility

Public GitHub repository visibility is a GitHub UI (or `gh`) action. Do not flip it until the history and secret reviews pass.

- [x] Confirm the default branch is `main` and CI/security workflows are green.
- [x] Decide whether issues, discussions, and wiki should stay enabled.
- [ ] Set repository visibility to public only after the checks in this document.
- [ ] After the change, clone anonymously over HTTPS and confirm the tree matches expectations.

Public source visibility does **not** change GHCR package visibility automatically.

## GitHub Actions secrets

- [x] Open the GitHub repo **Settings → Secrets and variables → Actions**.
- [x] Confirm there are no leftover personal tokens stored as Actions secrets that would leak by workflow misuse.
- [x] Remember that `GITHUB_TOKEN` is provided by GitHub for workflows such as GHCR login; its value is not in this repository.
- [x] Review workflow permissions in `.github/workflows/` (`contents: read`, `packages: write` on image publish).
- [x] After going public, consider whether any Actions secret used only for private-repo access can be deleted.

## GHCR package visibility

Image publish workflow: `.github/workflows/image-publish.yml`

Published name pattern:

```text
ghcr.io/tasi-ts/tenderops-api
```

- [x] Open GitHub **Packages** for `tenderops-api`.
- [x] Review package visibility separately from repository visibility.
- [ ] If the kind cluster should pull without an imagePullSecret, set the package to public **and** confirm anonymous `docker pull` works.
- [x] Link the package to this repository if GitHub has not already linked it.
- [x] Confirm tags `main` and `sha-<commit>` match the publish workflow.
- [x] Keep Helm `values.yaml` on `tenderops-api:0.1.0` until you intentionally switch the chart to GHCR.

A public Git repo with a private GHCR package will still require pull credentials in kind.

## Argo CD repo credential cleanup

While this GitHub repository is private, Argo CD needs a read-only GitHub credential to clone `https://github.com/tasi-ts/tenderops-lab.git`.

After the repository is public:

- [ ] Confirm Argo CD can sync without repository credentials.
- [ ] Remove the Argo CD repository credential that stored the GitHub token.
- [ ] Rotate and delete the GitHub fine-grained token that was issued for Argo CD. Do not leave a live token in the cluster or in GitHub if it is unused.
- [ ] Confirm `kubectl get applications -n argocd` still shows Synced/Healthy.

The token, if it existed, was a cluster/runtime credential. It must never be committed.

## Final validation

Run from the repository root after documentation and code freeze:

```bash
git status --short
./scripts/ci/api-check.sh
helm lint charts/tenderops
helm template tenderops charts/tenderops --namespace tenderops \
  | grep -nE 'kind: Secret|POSTGRES_PASSWORD|SPRING_DATASOURCE_PASSWORD' || true
./scripts/security/scan-local.sh
```

On the kind lab (if it is running):

```bash
kubectl get applications -n argocd
kubectl get pods -n tenderops
kubectl port-forward -n tenderops svc/tenderops-api 8080:8080
```

- [x] Retired hiring-process wording is absent from tracked files.
- [x] Helm lint passes.
- [x] Default Helm render does not create Secret objects or inline database passwords.
- [x] CI and security GitHub Actions are green on `main`.
- [x] README, architecture, observability, walkthrough, and this checklist are current.

## Notes before public release

- Demo credentials (`tenderops` / `tenderops` for Compose and runtime Secrets, Grafana `admin` / `admin`) are acceptable only as labeled local-lab values.
- Do not paste Argo CD initial admin passwords, kubeconfigs, or `argocd login` secrets into docs or screenshots.
- Making the repo public is not a production launch. Production gaps remain: auth, TLS, external secrets, backups, alerting, and cloud infrastructure.
- Terraform/Azure is an optional later segment and is not required for `v0.1.0`.

## v0.1.0 release tag

Create the annotated tag only after the checklist above is complete and GitHub Actions on `main` are green.

Do **not** create the tag or a GitHub Release as part of documentation-only work.

Suggested commands (run manually when ready):

```bash
git status --short
git checkout main
git pull
git tag -a v0.1.0 -m "TenderOps Lab v0.1.0"
git push origin v0.1.0
```

- [ ] Working tree is clean.
- [x] You are on up-to-date `main`.
- [ ] README, architecture, observability, walkthrough, demo commands, and this checklist are on `main`.
- [x] CI workflow is green.
- [x] Security workflow is green.
- [x] Image publish has succeeded if you want GHCR tags to exist for the same commit.
- [ ] Annotated tag `v0.1.0` is pushed.
- [x] GitHub Release creation is optional and separate; do not auto-create it from this task.

If the tag needs to move after a mistake, treat retagging as an explicit operator decision. Do not force-push `main`.
