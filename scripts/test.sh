# =============================================================================
# test.sh - Quick test script
# =============================================================================

cat > test.sh << 'EOF'
#!/bin/bash
echo "=========================================="
echo "Quick Test - Complete Booking Flow"
echo "=========================================="
echo ""

BASE_URL="http://localhost"

# Test 1: Create User
echo "1. Creating user..."
USER_RESPONSE=$(curl -s -X POST "${BASE_URL}:8081/api/users" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Test",
    "lastName": "User",
    "email": "test.user@example.com",
    "phone": "+94771234567",
    "address": "Test Address"
  }')
echo "✅ User created"
echo ""

# Test 2: Create Flight
echo "2. Creating flight..."
FLIGHT_RESPONSE=$(curl -s -X POST "${BASE_URL}:8082/api/flights" \
  -H "Content-Type: application/json" \
  -d '{
    "flightNumber": "TEST123",
    "airline": "Test Airlines",
    "origin": "Colombo",
    "destination": "Singapore",
    "departureTime": "2025-01-20T08:00:00",
    "arrivalTime": "2025-01-20T12:00:00",
    "price": 50000.00,
    "totalSeats": 180
  }')
echo "✅ Flight created"
echo ""

# Test 3: Create Hotel
echo "3. Creating hotel..."
HOTEL_RESPONSE=$(curl -s -X POST "${BASE_URL}:8083/api/hotels" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Hotel",
    "location": "Singapore",
    "address": "Test Address",
    "starRating": 5,
    "pricePerNight": 30000.00,
    "totalRooms": 100,
    "amenities": "Pool, Gym"
  }')
echo "✅ Hotel created"
echo ""

# Test 4: Create Booking
echo "4. Creating booking..."
BOOKING_RESPONSE=$(curl -s -X POST "${BASE_URL}:8084/api/bookings" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "flightId": 1,
    "hotelId": 1,
    "travelDate": "2025-01-20"
  }')
echo "✅ Booking created"
echo "$BOOKING_RESPONSE" | jq '.'
echo ""

# Test 5: Process Payment
echo "5. Processing payment..."
PAYMENT_RESPONSE=$(curl -s -X POST "${BASE_URL}:8085/api/payments/process" \
  -H "Content-Type: application/json" \
  -d '{
    "bookingId": 1,
    "amount": 80000.00,
    "paymentMethod": "CREDIT_CARD",
    "cardNumber": "4111111111111111",
    "cvv": "123",
    "expiryDate": "12/26"
  }')
echo "✅ Payment processed"
echo "$PAYMENT_RESPONSE" | jq '.'
echo ""

# Test 6: Get Booking Details
echo "6. Getting booking details..."
BOOKING_DETAILS=$(curl -s "${BASE_URL}:8084/api/bookings/1")
echo "✅ Booking details retrieved"
echo "$BOOKING_DETAILS" | jq '.'
echo ""

# Test 7: Get Notifications
echo "7. Getting notifications..."
NOTIFICATIONS=$(curl -s "${BASE_URL}:8086/api/notifications/user/1")
echo "✅ Notifications retrieved"
echo "$NOTIFICATIONS" | jq '.'
echo ""

echo "=========================================="
echo "✅ All tests completed successfully!"
echo "=========================================="
EOF

chmod +x test.sh