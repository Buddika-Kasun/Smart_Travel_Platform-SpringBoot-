# Smart Travel Booking Platform

A distributed microservices-based travel booking system built with Spring Boot 3+, Java 17+, featuring inter-service communication using REST API, Feign Client, and WebClient.

## 📋 Table of Contents
- [Architecture](#architecture)
- [Technologies Used](#technologies-used)
- [Microservices](#microservices)
- [Communication Patterns](#communication-patterns)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Project Structure](#project-structure)

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        Client Application                        │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
                    ┌────────────────┐
                    │    Booking     │ (Main Orchestrator)
                    │    Service     │ Port: 8084
                    │   (Port 8084)  │
                    └────────┬───────┘
                             │
           ┌─────────────────┼─────────────────┐
           │                 │                 │
           ▼                 ▼                 ▼
   ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
   │     User     │  │    Flight    │  │    Hotel     │
   │   Service    │  │   Service    │  │   Service    │
   │  (WebClient) │  │(Feign Client)│  │(Feign Client)│
   │  Port: 8081  │  │  Port: 8082  │  │  Port: 8083  │
   └──────────────┘  └──────────────┘  └──────────────┘
           │                                     │
           └─────────────────┬───────────────────┘
                             │
                   ┌─────────┴─────────┐
                   │                   │
                   ▼                   ▼
           ┌──────────────┐    ┌──────────────┐
           │   Payment    │    │ Notification │
           │   Service    │    │   Service    │
           │ (WebClient)  │    │ (WebClient)  │
           │  Port: 8085  │    │  Port: 8086  │
           └──────────────┘    └──────────────┘
                   │
                   └──────────────────┐
                                      ▼
                            ┌────────────────┐
                            │    Booking     │
                            │  Status Update │
                            │  (WebClient)   │
                            └────────────────┘
```

## 🛠️ Technologies Used

- **Java**: 17+
- **Spring Boot**: 3.2.0
- **Spring Cloud**: 2023.0.0
- **Database**: PostgreSQL 15
- **Build Tool**: Maven
- **Containerization**: Docker & Docker Compose
- **API Documentation**: Swagger/OpenAPI 3.0
- **Communication**:
    - Spring WebClient (Reactive)
    - OpenFeign Client
    - REST Controllers

## 🎯 Microservices

### 1. User Service (Port 8081)
- **Database**: user_db (PostgreSQL - Port 5432)
- **Endpoints**:
    - `POST /api/users` - Create user
    - `GET /api/users/{id}` - Get user by ID
    - `GET /api/users` - Get all users
    - `PUT /api/users/{id}` - Update user
    - `GET /api/users/validate/{id}` - Validate user

### 2. Flight Service (Port 8082)
- **Database**: flight_db (PostgreSQL - Port 5433)
- **Endpoints**:
    - `POST /api/flights` - Create flight
    - `GET /api/flights/{id}` - Get flight by ID
    - `GET /api/flights` - Get all flights
    - `GET /api/flights/{id}/availability` - Check availability
    - `POST /api/flights/{id}/reserve` - Reserve seat

### 3. Hotel Service (Port 8083)
- **Database**: hotel_db (PostgreSQL - Port 5434)
- **Endpoints**:
    - `POST /api/hotels` - Create hotel
    - `GET /api/hotels/{id}` - Get hotel by ID
    - `GET /api/hotels` - Get all hotels
    - `GET /api/hotels/{id}/availability` - Check availability
    - `POST /api/hotels/{id}/reserve` - Reserve room

### 4. Booking Service (Port 8084) - Main Orchestrator
- **Database**: booking_db (PostgreSQL - Port 5435)
- **Endpoints**:
    - `POST /api/bookings` - Create booking
    - `GET /api/bookings/{id}` - Get booking by ID
    - `GET /api/bookings/user/{userId}` - Get user bookings
    - `PUT /api/bookings/{id}/status` - Update booking status

### 5. Payment Service (Port 8085)
- **Database**: payment_db (PostgreSQL - Port 5436)
- **Endpoints**:
    - `POST /api/payments/process` - Process payment
    - `GET /api/payments/{id}` - Get payment by ID
    - `GET /api/payments/booking/{bookingId}` - Get payment by booking ID

### 6. Notification Service (Port 8086)
- **Database**: notification_db (PostgreSQL - Port 5437)
- **Endpoints**:
    - `POST /api/notifications/send` - Send notification
    - `GET /api/notifications/user/{userId}` - Get user notifications

## 🔄 Communication Patterns

### WebClient Communication
1. **Booking → User** (User Validation)
    - Type: HTTP GET Request
    - Purpose: Validate user exists and is active
    - Implementation: Spring WebClient

2. **Booking → Notification** (Send Notification)
    - Type: HTTP POST Request
    - Purpose: Send booking confirmation notification
    - Implementation: Spring WebClient

3. **Payment → Booking** (Update Status)
    - Type: HTTP PUT Request
    - Purpose: Update booking status after payment
    - Implementation: Spring WebClient

### Feign Client Communication
1. **Booking → Flight** (Check Availability & Reserve)
    - Type: Declarative HTTP Calls
    - Purpose: Check flight availability and reserve seats
    - Implementation: OpenFeign Client

2. **Booking → Hotel** (Check Availability & Reserve)
    - Type: Declarative HTTP Calls
    - Purpose: Check hotel availability and reserve rooms
    - Implementation: OpenFeign Client

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.8+
- Docker & Docker Compose
- PostgreSQL (if running locally without Docker)
- Postman (for API testing)

## 🚀 Installation & Setup

### Option 1: Using Docker Compose (Recommended)

1. **Clone the repository**
```bash
git clone <repository-url>
cd smart-travel-platform
```

2. **Build all services**
```bash
# Build each service
cd user-service && mvn clean package -DskipTests && cd ..
cd flight-service && mvn clean package -DskipTests && cd ..
cd hotel-service && mvn clean package -DskipTests && cd ..
cd booking-service && mvn clean package -DskipTests && cd ..
cd payment-service && mvn clean package -DskipTests && cd ..
cd notification-service && mvn clean package -DskipTests && cd ..
```

3. **Start all services with Docker Compose**
```bash
docker-compose up -d
```

4. **Verify all services are running**
```bash
docker-compose ps
```

5. **View logs**
```bash
docker-compose logs -f
```

6. **Stop all services**
```bash
docker-compose down
```

### Option 2: Running Locally

1. **Start PostgreSQL databases**
```bash
# Create databases
createdb user_db
createdb flight_db
createdb hotel_db
createdb booking_db
createdb payment_db
createdb notification_db
```

2. **Run each service**
```bash
# Terminal 1 - User Service
cd user-service
mvn spring-boot:run

# Terminal 2 - Flight Service
cd flight-service
mvn spring-boot:run

# Terminal 3 - Hotel Service
cd hotel-service
mvn spring-boot:run

# Terminal 4 - Booking Service
cd booking-service
mvn spring-boot:run

# Terminal 5 - Payment Service
cd payment-service
mvn spring-boot:run

# Terminal 6 - Notification Service
cd notification-service
mvn spring-boot:run
```

## 📚 API Documentation

Each service has Swagger UI documentation available at:
- User Service: http://localhost:8081/swagger-ui.html
- Flight Service: http://localhost:8082/swagger-ui.html
- Hotel Service: http://localhost:8083/swagger-ui.html
- Booking Service: http://localhost:8084/swagger-ui.html
- Payment Service: http://localhost:8085/swagger-ui.html
- Notification Service: http://localhost:8086/swagger-ui.html

## 🧪 Testing

### Complete Booking Flow Test

**Step 1: Create a User**
```bash
POST http://localhost:8081/api/users
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phone": "+94771234567",
  "address": "123 Main St, Colombo"
}
```

**Step 2: Create a Flight**
```bash
POST http://localhost:8082/api/flights
Content-Type: application/json

{
  "flightNumber": "SL123",
  "airline": "SriLankan Airlines",
  "origin": "Colombo",
  "destination": "Singapore",
  "departureTime": "2025-01-10T08:00:00",
  "arrivalTime": "2025-01-10T12:00:00",
  "price": 45000.00,
  "totalSeats": 180
}
```

**Step 3: Create a Hotel**
```bash
POST http://localhost:8083/api/hotels
Content-Type: application/json

{
  "name": "Marina Bay Sands",
  "location": "Singapore",
  "address": "10 Bayfront Avenue",
  "starRating": 5,
  "pricePerNight": 35000.00,
  "totalRooms": 2561,
  "amenities": "Pool, Spa, Casino, Restaurants"
}
```

**Step 4: Create a Booking**
```bash
POST http://localhost:8084/api/bookings
Content-Type: application/json

{
  "userId": 1,
  "flightId": 1,
  "hotelId": 1,
  "travelDate": "2025-01-10"
}
```

**Step 5: Process Payment**
```bash
POST http://localhost:8085/api/payments/process
Content-Type: application/json

{
  "bookingId": 1,
  "amount": 80000.00,
  "paymentMethod": "CREDIT_CARD",
  "cardNumber": "4111111111111111",
  "cvv": "123",
  "expiryDate": "12/26"
}
```

**Step 6: Verify Booking Confirmation**
```bash
GET http://localhost:8084/api/bookings/1
```

**Step 7: Check Notifications**
```bash
GET http://localhost:8086/api/notifications/user/1
```

## 📁 Project Structure

```
smart-travel-platform/
├── user-service/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/travel/userservice/
│   │       │   ├── controller/
│   │       │   ├── service/
│   │       │   ├── entity/
│   │       │   ├── repository/
│   │       │   ├── dto/
│   │       │   ├── exception/
│   │       │   └── UserServiceApplication.java
│   │       └── resources/
│   │           └── application.yml
│   ├── Dockerfile
│   └── pom.xml
├── flight-service/
│   ├── src/...
│   ├── Dockerfile
│   └── pom.xml
├── hotel-service/
│   ├── src/...
│   ├── Dockerfile
│   └── pom.xml
├── booking-service/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/travel/bookingservice/
│   │       │   ├── controller/
│   │       │   ├── service/
│   │       │   ├── entity/
│   │       │   ├── repository/
│   │       │   ├── dto/
│   │       │   ├── client/ (Feign Clients)
│   │       │   ├── config/ (WebClient Config)
│   │       │   └── BookingServiceApplication.java
│   │       └── resources/
│   │           └── application.yml
│   ├── Dockerfile
│   └── pom.xml
├── payment-service/
│   ├── src/...
│   ├── Dockerfile
│   └── pom.xml
├── notification-service/
│   ├── src/...
│   ├── Dockerfile
│   └── pom.xml
├── docker-compose.yml
├── postman_collection.json
└── README.md
```

## 🔍 Booking Flow Details

1. **User Request**: Client sends booking request with userId, flightId, hotelId, and travelDate
2. **User Validation**: Booking service validates user via WebClient call to User Service
3. **Flight Check**: Booking service checks flight availability via Feign Client
4. **Hotel Check**: Booking service checks hotel availability via Feign Client
5. **Cost Calculation**: Total cost = Flight price + Hotel price per night
6. **Booking Creation**: Creates booking with PENDING status
7. **Reservation**: Reserves flight seat and hotel room
8. **Payment Processing**: Payment service processes payment and updates booking status
9. **Notification**: Notification service sends confirmation email/SMS
10. **Status Update**: Booking status updated to CONFIRMED

## 🐛 Troubleshooting

### Common Issues

1. **Port already in use**
```bash
# Find and kill process using port
lsof -ti:8081 | xargs kill -9
```

2. **Database connection issues**
```bash
# Check PostgreSQL is running
docker-compose ps

# Restart databases
docker-compose restart user-db flight-db hotel-db booking-db payment-db notification-db
```

3. **Service communication errors**
- Check all services are running
- Verify service URLs in application.yml
- Check Docker network connectivity

## 📊 Health Checks

All services expose health check endpoints:
```bash
# User Service
curl http://localhost:8081/actuator/health

# Flight Service
curl http://localhost:8082/actuator/health

# Hotel Service
curl http://localhost:8083/actuator/health

# Booking Service
curl http://localhost:8084/actuator/health

# Payment Service
curl http://localhost:8085/actuator/health

# Notification Service
curl http://localhost:8086/actuator/health
```

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is part of the ITS 4243 - Microservices and Cloud Computing course at the University of Sri Jayewardenepura.

## 👥 Author

[Your Name]  
University of Sri Jayewardenepura  
Faculty of Technology  
Department of Information Communication Technology

---

**Assignment**: ITS 4243 - Assignment 02  
**Submission Date**: December 13, 2025