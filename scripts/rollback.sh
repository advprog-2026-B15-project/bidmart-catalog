#!/bin/bash
# BidMart Rollback & Disaster Recovery Script

echo "Starting Emergency Rollback Procedure..."

# 1. Back up current logs
mkdir -p ./backups/logs_$(date +%Y%m%d_%H%M%S)
docker compose logs > ./backups/logs_$(date +%Y%m%d_%H%M%S)/crash_report.log

# 2. Stop current containers
docker compose down

# 3. Revert to previous stable git state
echo "Reverting to the previous commit..."
# Simpan commit ID saat ini untuk referensi jika ingin membatalkan rollback
CURRENT_COMMIT=$(git rev-parse HEAD)
echo "Current failed commit: $CURRENT_COMMIT"

# Revert satu commit ke belakang secara otomatis (hard reset ke HEAD~1)
# Peringatan: Ini akan menghapus perubahan yang tidak di-commit di server.
git reset --hard HEAD~1

# 4. Restart services with clean build
echo "Rebuilding and restarting services..."
docker compose up -d --build

echo "Services have been restarted. Checking health..."
sleep 15
docker compose ps

echo "Rollback complete. If you need to return to the previous state, use: git reset --hard $CURRENT_COMMIT"
