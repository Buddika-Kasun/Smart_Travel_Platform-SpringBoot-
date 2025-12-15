# =============================================================================
# health-check.sh - Check health of all services
# =============================================================================

cat > health-check.sh << 'EOF'
#!/bin/bash
echo "=========================================="
echo "Health Check - All Services"
echo "=========================================="
echo ""

SERVICES=(
  "User Service:8081"
  "Flight Service:8082"
  "Hotel Service:8083"
  "Booking Service:8084"
  "Payment Service:8085"
  "Notification Service:8086"
)

for service in "${SERVICES[@]}"; do
    IFS=':' read -r name port <<< "$service"
    response=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:${port}/actuator/health")

    if [ "$response" == "200" ]; then
        echo "✅ $name (Port $port) - Healthy"
    else
        echo "❌ $name (Port $port) - Unhealthy (HTTP $response)"
    fi
done

echo ""
echo "=========================================="
EOF

chmod +x health-check.sh

echo "Setup scripts created:"
echo "  ✅ setup.sh        - Complete setup"
echo "  ✅ stop.sh         - Stop all services"
echo "  ✅ restart.sh      - Restart all services"
echo "  ✅ logs.sh         - View service logs"
echo "  ✅ test.sh         - Run quick tests"
echo "  ✅ health-check.sh - Check service health"
echo ""

# =============================================================================
# .env file for Docker Compose
# =============================================================================

cat > .env << 'EOF'
# Database Configuration
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres

# Service Ports
USER_SERVICE_PORT=8081
FLIGHT_SERVICE_PORT=8082
HOTEL_SERVICE_PORT=8083
BOOKING_SERVICE_PORT=8084
PAYMENT_SERVICE_PORT=8085
NOTIFICATION_SERVICE_PORT=8086

# Database Ports
USER_DB_PORT=5432
FLIGHT_DB_PORT=5433
HOTEL_DB_PORT=5434
BOOKING_DB_PORT=5435
PAYMENT_DB_PORT=5436
NOTIFICATION_DB_PORT=5437
EOF

echo "✅ Environment file created"
echo ""

# =============================================================================
# Create Dockerfile for each service (template)
# =============================================================================

cat > Dockerfile.template << 'EOF'
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy the JAR file
COPY target/*.jar app.jar

# Expose the port
EXPOSE ${PORT}

# Add health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT}/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
EOF

echo "✅ Dockerfile template created"
echo ""

echo "=========================================="
echo "🎉 Setup Complete!"
echo "=========================================="
