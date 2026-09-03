# Docker

Build the API image from the repository root:

```bash
docker build -t tenderops-api:0.1.0 ./src/api
```

Run API and PostgreSQL together with Compose (also from the repository root):

```bash
docker compose up --build -d
```

The API container needs a database and datasource environment. Do not run the image alone with `docker run` unless you supply those values.

Compose maps API `8080:8080` and PostgreSQL `5433:5432`. Demo credentials in `compose.yaml` are local-only.

Stop the stack:

```bash
docker compose down
```

Endpoints, health checks, and the Kubernetes image-load path: [docs/demo-commands.md](../docs/demo-commands.md) and [src/api/README.md](../src/api/README.md).
