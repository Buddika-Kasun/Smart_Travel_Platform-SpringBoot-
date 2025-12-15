# 📦 Submission Package - Smart Travel Booking Platform

## ✅ Submission Checklist

### 1. **Source Code** ✓
- [x] All 6 microservices with complete implementation
- [x] User Service (Port 8081)
- [x] Flight Service (Port 8082)
- [x] Hotel Service (Port 8083)
- [x] Booking Service (Port 8084) - Main Orchestrator
- [x] Payment Service (Port 8085)
- [x] Notification Service (Port 8086)

### 2. **Communication Implementation** ✓
- [x] **WebClient** used for:
    - Booking → User (validation)
    - Booking → Notification (send notification)
    - Payment → Booking (status update)
- [x] **Feign Client** used for:
    - Booking → Flight (availability check & reservation)
    - Booking → Hotel (availability check & reservation)
- [x] **REST Controllers** for all endpoints
- [x] No deprecated technologies (No RestTemplate)

### 3. **Database Configuration** ✓
- [x] 6 separate PostgreSQL databases
- [x] Each service has independent database
- [x] Database schema auto-created via Hibernate
- [x] Data isolation between services

### 4. **Docker Implementation** ✓
- [x] docker-compose.yml for complete setup
- [x] Individual Dockerfiles for each service
- [x] PostgreSQL containers for each database
- [x] Docker networking configured
- [x] Health checks implemented
- [x] Volume persistence for databases

### 5. **Code Structure** ✓
- [x] Controllers (REST endpoints)
- [x] Services (Business logic)
- [x] Repositories (Data access)
- [x] Entities (JPA entities)
- [x] DTOs (Data transfer objects)
- [x] Exception Handling (Global handlers)
- [x] Response Models (Standardized responses)

### 6. **Optional Features (Implemented)** ✓
- [x] Swagger/OpenAPI documentation for all services
- [x] Spring Boot Actuator health endpoints
- [x] Separate databases for each service
- [x] Docker containerization
- [x] Docker Compose orchestration

### 7. **Documentation** ✓
- [x] README.md with complete instructions
- [x] Architecture diagram
- [x] Communication flow documentation
- [x] Setup instructions
- [x] API endpoint documentation
- [x] Troubleshooting guide

### 8. **Testing Resources** ✓
- [x] Postman collection with all API calls
- [x] Complete booking flow test scenario
- [x] Individual service tests
- [x] Automated test script (test.sh)

### 9. **Setup Scripts** ✓
- [x] setup.sh - Complete setup automation
- [x] stop.sh - Stop all services
- [x] restart.sh - Restart services
- [x] logs.sh - View service logs
- [x] test.sh - Quick testing
- [x] health-check.sh - Service health monitoring

### 10. **GitHub Repository** ✓
- [x] Well-organized folder structure
- [x] .gitignore configured
- [x] README.md at root
- [x] All source code committed
- [x] Documentation files included

---

## 🏗️ Detailed Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                            CLIENT APPLICATION                                │
│                    (Postman / Web UI / Mobile App)                          │
└───────────────────────────────────┬─────────────────────────────────────────┘
                                    │
                                    │ HTTP/REST
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                          BOOKING SERVICE (8084)                              │
│                          Main Orchestrator Service                           │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  Controllers:                                                       │    │
│  │    • POST /api/bookings - Create booking                           │    │
│  │    • GET  /api/bookings/{id} - Get booking                         │    │
│  │    • PUT  /api/bookings/{id}/status - Update status                │    │
│  ├────────────────────────────────────────────────────────────────────┤    │
│  │  Communication Components:                                          │    │
│  │    • WebClient Bean (Reactive HTTP)                                │    │
│  │    • Feign Clients (Flight & Hotel)                                │    │
│  │    • Service Layer (Orchestration Logic)                           │    │
│  └────────────────────────────────────────────────────────────────────┘    │
└───────┬──────────────┬──────────────┬──────────────┬────────────────────────┘
        │              │              │              │
        │ WebClient    │ Feign        │ Feign        │ WebClient
        │ GET          │ GET/POST     │ GET/POST     │ POST
        ▼              ▼              ▼              ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│     USER     │ │    FLIGHT    │ │    HOTEL     │ │ NOTIFICATION │
│   SERVICE    │ │   SERVICE    │ │   SERVICE    │ │   SERVICE    │
│  (Port 8081) │ │  (Port 8082) │ │  (Port 8083) │ │  (Port 8086) │
├──────────────┤ ├──────────────┤ ├──────────────┤ ├──────────────┤
│ Endpoints:   │ │ Endpoints:   │ │ Endpoints:   │ │ Endpoints:   │
│ • Validate   │ │ • Availability│ │ • Availability│ │ • Send       │
│ • Get User   │ │ • Reserve    │ │ • Reserve    │ │ • Get List   │
│ • CRUD Ops   │ │ • CRUD Ops   │ │ • CRUD Ops   │ │              │
├──────────────┤ ├──────────────┤ ├──────────────┤ ├──────────────┤
│   Database   │ │   Database   │ │   Database   │ │   Database   │
│   user_db    │ │  flight_db   │ │  hotel_db    │ │notification_db│
│ (Port 5432)  │ │ (Port 5433)  │ │ (Port 5434)  │ │ (Port 5437)  │
└──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘

        ┌──────────────────────────────────────────────┐
        │          PAYMENT SERVICE (8085)               │
        │                                               │
        │  Endpoints:                                   │
        │    • POST /api/payments/process               │
        │    • GET  /api/payments/{id}                  │
        │                                               │
        │  WebClient (Reactive)                         │
        │    └──> PUT /api/bookings/{id}/status         │
        │         (Updates booking to CONFIRMED/FAILED) │
        │                                               │
        │  Database: payment_db (Port 5436)             │
        └───────────────────┬───────────────────────────┘
                            │
                            │ WebClient PUT
                            ▼
                   ┌────────────────┐
                   │    BOOKING     │
                   │    SERVICE     │
                   │ Status Update  │
                   └────────────────┘
```

---

## 🔄 Communication Flow - Complete Booking Scenario

### **Step-by-Step Flow:**

```
1. CLIENT → BOOKING SERVICE
   POST /api/bookings
   Body: { userId, flightId, hotelId, travelDate }
   
2. BOOKING SERVICE → USER SERVICE (WebClient)
   GET /api/users/validate/{userId}
   Response: { valid: true/false }
   
3. BOOKING SERVICE → FLIGHT SERVICE (Feign Client)
   GET /api/flights/{flightId}/availability
   Response: { available: true, price: 45000, seats: 50 }
   
4. BOOKING SERVICE → HOTEL SERVICE (Feign Client)
   GET /api/hotels/{hotelId}/availability
   Response: { available: true, price: 35000, rooms: 20 }
   
5. BOOKING SERVICE - Internal Processing
   • Calculate total cost: Flight + Hotel
   • Create booking with status: PENDING
   • Save to booking_db
   • Generate booking reference
   
6. BOOKING SERVICE → FLIGHT SERVICE (Feign Client)
   POST /api/flights/{flightId}/reserve
   Response: { success: true }
   
7. BOOKING SERVICE → HOTEL SERVICE (Feign Client)
   POST /api/hotels/{hotelId}/reserve
   Response: { success: true }
   
8. CLIENT → PAYMENT SERVICE
   POST /api/payments/process
   Body: { bookingId, amount, paymentMethod, cardDetails }
   
9. PAYMENT SERVICE - Internal Processing
   • Simulate payment gateway
   • Save payment record
   • Generate transaction ID
   
10. PAYMENT SERVICE → BOOKING SERVICE (WebClient)
    PUT /api/bookings/{bookingId}/status?status=CONFIRMED
    Response: { status: CONFIRMED }
    
11. BOOKING SERVICE → NOTIFICATION SERVICE (WebClient)
    POST /api/notifications/send
    Body: { userId, title, message }
    
12. NOTIFICATION SERVICE - Internal Processing
    • Save notification
    • Simulate email/SMS sending
    • Update status to SENT
    
13. BOOKING SERVICE → CLIENT
    Response: { bookingId, reference, status: CONFIRMED, totalCost }
```

---

## 📊 Database Schema Overview

### **User Service Database (user_db)**
```sql
Table: users
- id (PK, BIGSERIAL)
- first_name (VARCHAR)
- last_name (VARCHAR)
- email (VARCHAR, UNIQUE)
- phone (VARCHAR)
- address (TEXT)
- active (BOOLEAN)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)
```

### **Flight Service Database (flight_db)**
```sql
Table: flights
- id (PK, BIGSERIAL)
- flight_number (VARCHAR, UNIQUE)
- airline (VARCHAR)
- origin (VARCHAR)
- destination (VARCHAR)
- departure_time (TIMESTAMP)
- arrival_time (TIMESTAMP)
- price (DECIMAL)
- total_seats (INTEGER)
- available_seats (INTEGER)
- active (BOOLEAN)
- created_at (TIMESTAMP)
```

### **Hotel Service Database (hotel_db)**
```sql
Table: hotels
- id (PK, BIGSERIAL)
- name (VARCHAR)
- location (VARCHAR)
- address (TEXT)
- star_rating (INTEGER)
- price_per_night (DECIMAL)
- total_rooms (INTEGER)
- available_rooms (INTEGER)
- amenities (TEXT)
- active (BOOLEAN)
- created_at (TIMESTAMP)
```

### **Booking Service Database (booking_db)**
```sql
Table: bookings
- id (PK, BIGSERIAL)
- user_id (BIGINT)
- flight_id (BIGINT)
- hotel_id (BIGINT)
- travel_date (DATE)
- total_cost (DECIMAL)
- status (VARCHAR) -- PENDING, CONFIRMED, CANCELLED, FAILED
- booking_reference (VARCHAR)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)
```

### **Payment Service Database (payment_db)**
```sql
Table: payments
- id (PK, BIGSERIAL)
- booking_id (BIGINT)
- amount (DECIMAL)
- status (VARCHAR) -- PENDING, SUCCESS, FAILED, REFUNDED
- payment_method (VARCHAR) -- CREDIT_CARD, DEBIT_CARD, PAYPAL, etc.
- transaction_id (VARCHAR)
- created_at (TIMESTAMP)
```

### **Notification Service Database (notification_db)**
```sql
Table: notifications
- id (PK, BIGSERIAL)
- user_id (BIGINT)
- title (VARCHAR)
- message (TEXT)
- type (VARCHAR) -- EMAIL, SMS, PUSH
- status (VARCHAR) -- PENDING, SENT, FAILED
- created_at (TIMESTAMP)
- sent_at (TIMESTAMP)
```

---

## 📦 Deliverables Summary

### **GitHub Repository Structure:**
```
smart-travel-platform/
├── user-service/
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── flight-service/
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── hotel-service/
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── booking-service/
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── payment-service/
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── notification-service/
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── docker-compose.yml
├── README.md
├── ARCHITECTURE.md
├── postman_collection.json
├── setup.sh
├── stop.sh
├── restart.sh
├── logs.sh
├── test.sh
├── health-check.sh
└── .gitignore
```

### **Files to Submit:**

1. **GitHub Repository URL**
2. **README.md** (Complete documentation)
3. **Postman Collection** (JSON file with all API tests)
4. **Architecture Diagram** (This document or separate PDF)
5. **Screenshots** showing:
    - All services running (docker-compose ps)
    - Swagger UI for each service
    - Successful booking creation
    - Payment processing
    - Notification sent
    - Database records created

---

## 🎯 Key Features Implemented

✅ **Spring Boot 3.2.0** with Java 17  
✅ **WebClient** (Reactive HTTP client)  
✅ **Feign Client** (Declarative REST client)  
✅ **6 Separate PostgreSQL Databases**  
✅ **Docker & Docker Compose**  
✅ **Swagger/OpenAPI Documentation**  
✅ **Complete Exception Handling**  
✅ **Standardized API Responses**  
✅ **Transaction Management**  
✅ **Service Health Checks**  
✅ **Comprehensive Logging**

---

## 🚀 Quick Start Commands

```bash
# Clone and setup
git clone <repository-url>
cd smart-travel-platform
chmod +x *.sh

# Build and start
./setup.sh

# Run tests
./test.sh

# Check health
./health-check.sh

# View logs
./logs.sh

# Stop services
./stop.sh
```

---

## 📞 Support & Contact

For any issues or questions:
- Check the troubleshooting section in README.md
- Review service logs: `./logs.sh [service-name]`
- Verify health: `./health-check.sh`

**Assignment:** ITS 4243 - Assignment 02  
**Due Date:** December 13, 2025  
**Institution:** University of Sri Jayewardenepura

---

**✅ All Requirements Met & Exceeded!**