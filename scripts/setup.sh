#!/bin/bash
# =============================================================================
# setup.sh - Complete Setup Script for Smart Travel Platform
# =============================================================================

echo "=========================================="
echo "Smart Travel Platform Setup"
echo "=========================================="
echo ""

# Check prerequisites
echo "Checking prerequisites..."

# Check Java
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 17 or higher."
    exit 1
fi
echo "✅ Java found: $(java -version 2>&1 | head -n 1)"

# Check Maven
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven 3.8+."
    exit 1
fi
echo "✅ Maven found: $(mvn -version | head -n 1)"

# Check Docker
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker."
    exit 1
fi
echo "✅ Docker found: $(docker --version)"

# Check Docker Compose
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose is not installed. Please install Docker Compose."
    exit 1
fi
echo "✅ Docker Compose found: $(docker-compose --version)"

echo ""
echo "All prerequisites satisfied!"
echo ""

# =============================================================================
# Build Script
# =============================================================================

echo "=========================================="
echo "Building all services..."
echo "=========================================="
echo ""

SERVICES=("user-service" "flight-service" "hotel-service" "booking-service" "payment-service" "notification-service")

for service in "${SERVICES[@]}"; do
    echo "Building $service..."
    cd $service
    mvn clean package -DskipTests
    if [ $? -eq 0 ]; then
        echo "✅ $service built successfully"
    else
        echo "❌ Failed to build $service"
        exit 1
    fi
    cd ..
    echo ""
done

echo "✅ All services built successfully!"
echo ""

# =============================================================================
# Docker Setup
# =============================================================================

echo "=========================================="
echo "Starting Docker containers..."
echo "=========================================="
echo ""

# Start all services
docker-compose up -d

if [ $? -eq 0 ]; then
    echo "✅ All services started successfully!"
else
    echo "❌ Failed to start services"
    exit 1
fi

echo ""
echo "Waiting for services to be ready..."
sleep 30

echo ""
echo "=========================================="
echo "Service Status"
echo "=========================================="
docker-compose ps

echo ""
echo "=========================================="
echo "Setup Complete!"
echo "=========================================="
echo ""
echo "Services are now running:"
echo "  • User Service:         http://localhost:8081"
echo "  • Flight Service:       http://localhost:8082"
echo "  • Hotel Service:        http://localhost:8083"
echo "  • Booking Service:      http://localhost:8084"
echo "  • Payment Service:      http://localhost:8085"
echo "  • Notification Service: http://localhost:8086"
echo ""
echo "Swagger UI available at:"
echo "  http://localhost:8081/swagger-ui.html (User)"
echo "  http://localhost:8082/swagger-ui.html (Flight)"
echo "  http://localhost:8083/swagger-ui.html (Hotel)"
echo "  http://localhost:8084/swagger-ui.html (Booking)"
echo "  http://localhost:8085/swagger-ui.html (Payment)"
echo "  http://localhost:8086/swagger-ui.html (Notification)"
echo ""
echo "To view logs:"
echo "  docker-compose logs -f"
echo ""
echo "To stop all services:"
echo "  docker-compose down"
echo ""
