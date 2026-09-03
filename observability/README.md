# Observability

Local kube-prometheus-stack values for the kind lab. Operational story, PromQL, and port-forwards: [docs/observability.md](../docs/observability.md). Command transcript: [docs/demo-commands.md](../docs/demo-commands.md).

## Files

```text
observability/kube-prometheus-stack-values.yaml
```

Local-only Grafana credentials in that file are `admin` / `admin`. Do not reuse them outside this lab.

Install (kind cluster already running), before Argo CD syncs the chart, so ServiceMonitor CRDs exist:

```bash
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm upgrade --install monitoring prometheus-community/kube-prometheus-stack \
  --namespace monitoring \
  --create-namespace \
  -f observability/kube-prometheus-stack-values.yaml
```

Expected Service names:

```text
monitoring-kube-prometheus-prometheus
monitoring-grafana
```

The API ServiceMonitor and Grafana dashboard ConfigMap are defined in the Helm chart, not in this directory.
