# =============================================================================
# restart.sh - Restart all services
# =============================================================================

cat > restart.sh << 'EOF'
#!/bin/bash
echo "Restarting all services..."
docker-compose restart
echo "✅ All services restarted"
EOF

chmod +x restart.sh