# Project Goals

TenderOps Lab is a local-first DevOps practice project.

The goal is not to build a complex business application. The goal is to demonstrate a realistic delivery workflow around a small Spring Boot API.

## Primary goals

- Build and test a Java/Spring Boot API with Maven.
- Package the API as a Docker image.
- Run the API and PostgreSQL locally with Docker Compose.
- Deploy the API and database into a local Kubernetes cluster.
- Package Kubernetes resources with Helm.
- Use Argo CD to demonstrate GitOps-style delivery.
- Expose application health and metrics through Spring Boot Actuator.
- Add local security scanning with Trivy.
- Document the project so it can be explained clearly in an interview.

## Non-goals

This project intentionally does not try to be a production tender-management system.

Out of scope for the current version:

- full frontend
- user authentication
- complex domain model
- production cloud deployment
- managed database
- external secret management
- full monitoring stack
- production-grade network policy model

These can be added later as extensions.
