 =============================================================================
# logs.sh - View logs
# =============================================================================

cat > logs.sh << 'EOF'
#!/bin/bash
if [ -z "$1" ]; then
    echo "Showing logs for all services..."
    docker-compose logs -f
else
    echo "Showing logs for $1..."
    docker-compose logs -f $1
fi
EOF

chmod +x logs.sh