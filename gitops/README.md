# GitOps

Argo CD Application manifests live in [gitops/apps](apps/README.md).

The active path is Helm chart → Argo CD → Kubernetes. Do not `helm install` / `helm upgrade` the TenderOps release during normal use; change the chart, commit, and let Argo CD sync.

Diagrams: [docs/architecture.md](../docs/architecture.md). Commands: [docs/demo-commands.md](../docs/demo-commands.md).
