.PHONY: help tree

help:
	@echo "TenderOps Lab commands"
	@echo ""
	@echo "Available targets:"
	@echo "  make tree     Show project structure"

tree:
	@find . -not -path "./.git/*" -not -path "." | sort

api-test:
	cd src/api && ./mvnw clean test

api-build:
	cd src/api && ./mvnw clean package

api-docker-build:
	docker build -t tenderops-api:0.1.0 ./src/api

api-docker-run:
	docker run --rm --name tenderops-api -p 8080:8080 tenderops-api:0.1.0
