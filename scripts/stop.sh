# =============================================================================
# stop.sh - Stop all services
# =============================================================================

cat > stop.sh << 'EOF'
#!/bin/bash
echo "Stopping all services..."
docker-compose down
echo "✅ All services stopped"
EOF

chmod +x stop.sh