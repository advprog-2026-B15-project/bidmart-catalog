#!/bin/bash
# BidMart Rollback & Disaster Recovery Script

echo "Starting Emergency Rollback Procedure..."

# 1. Back up current logs
mkdir -p ./backups/logs_$(date +%Y%m%d_%H%M%S)
docker compose logs > ./backups/logs_$(date +%Y%m%d_%H%M%S)/crash_report.log

# 2. Stop current containers
docker compose down

# 3. Revert to stable git state (if needed, default to previous commit)
# git revert HEAD --no-edit

# 4. Restart services with clean build
docker compose up -d --build

echo "Services have been restarted. Checking health..."
sleep 10
docker compose ps
