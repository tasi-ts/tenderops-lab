.PHONY: help tree

help:
@echo "TenderOps Lab commands"
@echo ""
@echo "Available targets:"
@echo "  make tree     Show project structure"

tree:
@find . -not -path "./.git/*" -not -path "." | sort
