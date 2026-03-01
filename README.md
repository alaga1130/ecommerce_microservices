# Ecommerce Saga Choreography Microservices

A production-ready microservices architecture demonstrating the **Saga choreography pattern** for distributed transactions. This project showcases how to build scalable, event-driven microservices with asynchronous communication, distributed tracing, and service discovery.

## 📋 Table of Contents

- [Project Overview](#project-overview)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Project Structure](#project-structure)
- [Services](#services)
- [API Endpoints](#api-endpoints)
- [Saga Flow](#saga-flow)
- [Configuration](#configuration)
- [Docker & Deployment](#docker--deployment)
- [Monitoring & Troubleshooting](#monitoring--troubleshooting)
- [Development Guide](#development-guide)

## 📖 Project Overview

This project demonstrates an **e-commerce order management system** using microservices architecture with the **Saga pattern** for managing distributed transactions across multiple services. When an order is placed, a saga orchestrates a series of events across payment, inventory, and shipping services to complete the transaction.

**Key Features:**
- ✅ Event-driven architecture using Kafka
- ✅ Saga choreography for distributed transactions
- ✅ Asynchronous + Synchronous communication patterns
- ✅ Service discovery with Eureka
- ✅ Centralized configuration with Config Server
- ✅ API Gateway for request routing
- ✅ Distributed tracing with Zipkin
- ✅ MongoDB for persistent storage
- ✅ Docker containerization
- ✅ Health checks and resilience

## 🛠 Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| **Language** | Java | 17 |
| **Framework** | Spring Boot | 3.x |
| **Cloud** | Spring Cloud | 2022.x |
| **Database** | MongoDB | 6 |
| **Message Broker** | Apache Kafka | 7.4.0 |
| **Service Discovery** | Eureka | 2022.x |
| **Configuration** | Spring Cloud Config | 2022.x |
| **API Gateway** | Spring Cloud Gateway | 2022.x |
| **Tracing** | Zipkin | Latest |
| **Container** | Docker & Docker Compose | 3.8 |
| **Build Tool** | Maven | 3.8+ |

## 🏗 Architecture

### System Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                    API Gateway (8080)                       │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐             │
│  │   Order    │  │  Payment   │  │ Inventory  │             │
│  │  Service   │  │  Service   │  │  Service   │             │
│  │   (8081)   │  │   (8082)   │  │   (8083)   │             │
│  └─────┬──────┘  └─────┬──────┘  └─────┬──────┘             │
│        │                │               │                   │
│        └────────────────┼───────────────┘                   │
│                         │                                   │
│        ┌────────────────┴────────────────┐                  │
│        │                                 │                  │
│  ┌────────────┐    ┌──────────────┐     │                  │
│  │  Shipping  │    │ Kafka Topics │     │                  │
│  │  Service   │    └──────────────┘     │                  │
│  │   (8084)   │                         │                  │
│  └────────────┘                         │                  │
│                                          │                  │
│  ┌──────────────────────────────────────┐                  │
│  │  Eureka Service Discovery (8761)     │                  │
│  └──────────────────────────────────────┘                  │
│                                                              │
│  ┌──────────────────────────────────────┐                  │
│  │  Config Server (8888) + Config Repo  │                  │
│  └──────────────────────────────────────┘                  │
└─────────────────────────────────────────────────────────────┘
         │                    │                    │
    ┌────▼────┐      ┌───────▼────┐      ┌───────▼────┐
    │  Order   │      │  Zipkin    │      │   Kafka   │
    │  MongoDB │      │ (9411)     │      │  (9092)   │
    └──────────┘      └────────────┘      └───────────┘
```

## 📋 Prerequisites

- **Java 17** or higher
- **Docker & Docker Compose** (version 20.x+)
- **Maven 3.8+**
- **Git**
- **Postman** (optional, for API testing)
- **4GB+ RAM** for Docker containers

### Installation

#### Windows
```powershell
# Check Java version
java -version

# Install Docker Desktop from https://www.docker.com/products/docker-desktop
# Verify Docker
docker --version
docker-compose --version
```

#### macOS
```bash
# Install Java (if not already installed)
brew install openjdk@17

# Install Docker Desktop
brew install --cask docker

# Verify installations
java -version
docker --version
```

#### Linux
```bash
# Install Java
sudo apt-get install openjdk-17-jdk

# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Verify installations
java -version
docker --version
```

## 🚀 Quick Start

### 1. Clone and Build Services

```bash
# Navigate to project directory
cd ecommerce_microservices_saga

# Build all services (this may take 5-10 minutes)
cd api-gateway && mvn clean package && cd ..
cd config-server && mvn clean package && cd ..
cd eureka-server && mvn clean package && cd ..
cd order-service && mvn clean package && cd ..
cd payment-service && mvn clean package && cd ..
cd inventory-service && mvn clean package && cd ..
cd shipping-service && mvn clean package && cd ..
```

### 2. Start All Services

```bash
# From project root
docker-compose up --build

# This will:
# - Build Docker images for all services
# - Start 7 microservices
# - Start 4 MongoDB instances with Mongo Express
# - Start Kafka, Zookeeper, and Zipkin
# - Create necessary Kafka topics
```

### 3. Verify Services Are Running

```bash
# Check all containers
docker-compose ps

# Expected output: All services should be "Up"
```

### 4. Test Services

Access the services via your browser or Postman:

- **API Gateway**: http://localhost:8080
- **Eureka Dashboard**: http://localhost:8761
- **Zipkin**: http://localhost:9411
- **Kafka UI**: http://localhost:8085
- **Config Server**: http://localhost:8888

## 📂 Project Structure

```
ecommerce_microservices_saga/
├── api-gateway/                 # Spring Cloud Gateway (Port 8080)
│   ├── src/main/java/...
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── bootstrap.yml
│   ├── Dockerfile
│   └── pom.xml
│
├── eureka-server/               # Service Discovery (Port 8761)
│   ├── src/main/java/...
│   ├── src/main/resources/
│   │   └── application.yml
│   ├── Dockerfile
│   └── pom.xml
│
├── config-server/               # Config Server (Port 8888)
│   ├── src/main/java/...
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── bootstrap.yml
│   ├── Dockerfile
│   └── pom.xml
│
├── order-service/               # Order Management (Port 8081)
│   ├── src/main/java/...
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── bootstrap.yml
│   ├── Dockerfile
│   └── pom.xml
│
├── payment-service/             # Payment Processing (Port 8082)
│   ├── src/main/java/...
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── bootstrap.yml
│   ├── Dockerfile
│   └── pom.xml
│
├── inventory-service/           # Inventory Management (Port 8083)
│   ├── src/main/java/...
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── bootstrap.yml
│   ├── Dockerfile
│   └── pom.xml
│
├── shipping-service/            # Shipping Management (Port 8084)
│   ├── src/main/java/...
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── bootstrap.yml
│   ├── Dockerfile
│   └── pom.xml
│
├── config-repo/                 # Centralized Configuration
│   ├── application.yml
│   ├── api-gateway.yml
│   ├── order-service.yml
│   ├── payment-service.yml
│   ├── inventory-service.yml
│   └── shipping-service.yml
│
├── docker-compose.yml           # Docker Compose Configuration
├── PostmanCollection.json       # API Testing Collection
├── microservices-collection.json # Alternative Postman Collection
└── README.md                    # This file
```

## 🔧 Services

### Order Service (Port 8081)

**Responsibility**: Create and manage customer orders

**Database**: MongoDB (orderdb)

**Key Endpoints**:
- `POST /orders` - Create a new order
- `GET /orders` - Get all orders
- `GET /orders/{orderId}` - Get order details
- `DELETE /orders/{orderId}` - Cancel an order
- `GET /orders/status/{status}` - Get orders by status
- `GET /orders/ping` - Health check

**Events Published**:
- `order-events` (Kafka topic)

---

### Payment Service (Port 8082)

**Responsibility**: Process payments and handle refunds

**Database**: MongoDB (paymentdb)

**Key Endpoints**:
- `POST /payments` - Process payment for an order
- `GET /payments` - Get all payments
- `GET /payments/{paymentId}` - Get payment details
- `GET /payments/ping` - Health check

**Events Published**:
- `payment-events` (Kafka topic)
- `refund-events` (Kafka topic)

---

### Inventory Service (Port 8083)

**Responsibility**: Manage product inventory and stock

**Database**: MongoDB (inventorydb)

**Key Endpoints**:
- `POST /inventory` - Add inventory for a product
- `GET /inventory` - Get all inventory
- `GET /inventory/{inventoryId}` - Get inventory details
- `GET /inventory/ping` - Health check

**Events Published**:
- `inventory-events` (Kafka topic)

---

### Shipping Service (Port 8084)

**Responsibility**: Manage shipment tracking and delivery

**Database**: MongoDB (shippingdb)

**Key Endpoints**:
- `POST /shipping` - Create shipment for an order
- `GET /shipping` - Get all shipments
- `GET /shipping/{shipmentId}` - Get shipment details
- `GET /shipping/ping` - Health check

**Events Published**:
- `shipping-events` (Kafka topic)

---

### API Gateway (Port 8080)

**Responsibility**: Route requests to appropriate microservices

**Routes**:
```
/orders/** → Order Service (8081)
/payments/** → Payment Service (8082)
/inventory/** → Inventory Service (8083)
/shipping/** → Shipping Service (8084)
```

---

### Config Server (Port 8888)

**Responsibility**: Centralized configuration management

**Features**:
- Native filesystem mode (reads from `config-repo/`)
- Health check endpoint: `/actuator/health`
- Provides dynamic configuration to all services

---

### Eureka Server (Port 8761)

**Responsibility**: Service discovery and registration

**Features**:
- Auto-registers all microservices
- Tracks service health
- Enables client-side load balancing

## 📡 API Endpoints

### Order Service Endpoints

| Method | Endpoint | Description | Body |
|--------|----------|-------------|------|
| **GET** | `/orders/ping` | Health check | - |
| **POST** | `/orders` | Create order | `{"productId": "P100", "quantity": 2}` |
| **GET** | `/orders` | Get all orders | - |
| **GET** | `/orders/{orderId}` | Get order by ID | - |
| **DELETE** | `/orders/{orderId}` | Cancel order | - |
| **GET** | `/orders/status/{status}` | Filter by status | - |

### Payment Service Endpoints

| Method | Endpoint | Description | Body |
|--------|----------|-------------|------|
| **GET** | `/payments/ping` | Health check | - |
| **POST** | `/payments` | Process payment | `{"orderId": "{{orderId}}", "amount": 500}` |
| **GET** | `/payments` | Get all payments | - |
| **GET** | `/payments/{paymentId}` | Get payment by ID | - |

### Inventory Service Endpoints

| Method | Endpoint | Description | Body |
|--------|----------|-------------|------|
| **GET** | `/inventory/ping` | Health check | - |
| **POST** | `/inventory` | Add inventory | `{"productId": "P100", "availableQuantity": 10}` |
| **GET** | `/inventory` | Get all inventory | - |
| **GET** | `/inventory/{inventoryId}` | Get inventory by ID | - |

### Shipping Service Endpoints

| Method | Endpoint | Description | Body |
|--------|----------|-------------|------|
| **GET** | `/shipping/ping` | Health check | - |
| **POST** | `/shipping` | Create shipment | `{"orderId": "{{orderId}}"}` |
| **GET** | `/shipping` | Get all shipments | - |
| **GET** | `/shipping/{shipmentId}` | Get shipment by ID | - |

## 🔄 Saga Flow

The **Order to Shipment Saga** follows this orchestration pattern:

```
User Creates Order
    ↓
Order Service
    ↓
Publishes: order-events
    ↓
Payment Service (Listener)
    ├→ Publishes: payment-events
    ↓
Inventory Service (Listener)
    ├→ Publishes: inventory-events
    ↓
Shipping Service (Listener)
    ├→ Publishes: shipping-events
    ↓
Order Service (Listener)
    ├→ Updates Order Status (COMPLETED)
    ↓
Saga Complete
```

### Typical Order Flow

1. **User submits an order** via POST `/orders`
2. **Order Service** creates order with status `PENDING`
3. **Order Service publishes** `OrderCreatedEvent` to Kafka
4. **Payment Service** listens and processes payment
5. **Payment Service publishes** `PaymentProcessedEvent`
6. **Inventory Service** listens and reserves inventory
7. **Inventory Service publishes** `InventoryReservedEvent`
8. **Shipping Service** listens and creates shipment
9. **Shipping Service publishes** `ShipmentCreatedEvent`
10. **Order Service** receives all confirmations
11. **Order status updates** to `COMPLETED`

### Failure Handling

If any step fails (e.g., payment declined):
- Service publishes failure event
- Previous services listen for failure events
- Compensation logic executes (e.g., refund, inventory release)
- Order status updates to `FAILED` or `CANCELLED`

## ⚙️ Configuration

### Service Configuration

Each service has its own configuration file in `config-repo/`:

```yaml
# config-repo/order-service.yml
spring:
  application:
    name: order-service
  data:
    mongodb:
      uri: mongodb://order-mongo:27017/orderdb
  kafka:
    bootstrap-servers: kafka:9092
    
server:
  port: 8081
```

### Environment Profiles

- **Development**: `application.yml` (local)
- **Docker**: `application-docker.yml` (containerized)

Switch profiles:
```bash
# Local development
export SPRING_PROFILES_ACTIVE=default

# Docker environment
export SPRING_PROFILES_ACTIVE=docker
```

## 🐳 Docker & Deployment

### Docker Compose Services

| Service | Port | Image/Build | Purpose |
|---------|------|-------------|---------|
| **API Gateway** | 8080 | Custom | Request routing |
| **Order Service** | 8081 | Custom | Order management |
| **Payment Service** | 8082 | Custom | Payment processing |
| **Inventory Service** | 8083 | Custom | Stock management |
| **Shipping Service** | 8084 | Custom | Shipment tracking |
| **Eureka Server** | 8761 | Custom | Service discovery |
| **Config Server** | 8888 | Custom | Configuration |
| **Order MongoDB** | 27017 | mongo:6 | Order data |
| **Payment MongoDB** | 27018 | mongo:6 | Payment data |
| **Inventory MongoDB** | 27019 | mongo:6 | Inventory data |
| **Shipping MongoDB** | 27020 | mongo:6 | Shipping data |
| **Mongo Express** | 9091 | mongo-express | Order DB UI |
| **Mongo Express** | 9092 | mongo-express | Payment DB UI |
| **Mongo Express** | 9093 | mongo-express | Inventory DB UI |
| **Mongo Express** | 9094 | mongo-express | Shipping DB UI |
| **Kafka** | 9092 | confluentinc/cp-kafka | Message broker |
| **Zookeeper** | 2181 | confluentinc/cp-zookeeper | Kafka coordination |
| **Kafka UI** | 8085 | provectuslabs/kafka-ui | Kafka UI |
| **Zipkin** | 9411 | openzipkin/zipkin | Distributed tracing |

### Start Services

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Remove volumes (WARNING: deletes all data)
docker-compose down -v
```

### Build Individual Service

```bash
# Rebuild a specific service
docker-compose up -d --build order-service

# Force rebuild from scratch
docker-compose build --no-cache order-service
```

## 🔍 Monitoring & Troubleshooting

### Accessing Monitoring Tools

#### Eureka Dashboard
```
URL: http://localhost:8761
View: All registered services and their status
```

#### Zipkin Distributed Tracing
```
URL: http://localhost:9411
Steps:
1. Open Zipkin UI
2. Send requests via Postman
3. Click "Run Query"
4. View trace timeline and dependencies
```

#### Kafka UI
```
URL: http://localhost:8085
View: Topics, partitions, messages, consumer groups
```

#### MongoDB Explorer
```
Order Service: http://localhost:9091
Payment Service: http://localhost:9092
Inventory Service: http://localhost:9093
Shipping Service: http://localhost:9094
```

### Common Issues & Solutions

#### Issue: Services fail to start
```bash
# Check logs
docker-compose logs <service-name>

# Check if ports are in use
netstat -ano | findstr :<port>  # Windows
lsof -i :<port>                 # macOS/Linux

# Stop conflicting services and restart
docker-compose down
docker-compose up -d
```

#### Issue: MongoDB connection errors
```bash
# Verify MongoDB containers are running
docker-compose ps | grep mongo

# Check MongoDB logs
docker-compose logs order-mongo

# Ensure MongoDB is healthy
docker-compose exec order-mongo mongosh --eval "db.adminCommand('ping')"
```

#### Issue: Kafka topics not created
```bash
# Manually create topics
docker-compose exec kafka kafka-topics --create \
  --topic order-events \
  --bootstrap-server localhost:9092 \
  --partitions 1 \
  --replication-factor 1
```

#### Issue: Eureka shows "UNKNOWN" status
```bash
# Wait 30 seconds for services to register
# Check service logs for connection errors
docker-compose logs order-service | grep -i eureka

# Restart the service
docker-compose restart order-service
```

#### Issue: Build failures
```bash
# Clean all builds
cd order-service && mvn clean && cd ..
cd payment-service && mvn clean && cd ..
cd inventory-service && mvn clean && cd ..
cd shipping-service && mvn clean && cd ..

# Rebuild
docker-compose build --no-cache
```

### Health Checks

```bash
# Check API Gateway
curl http://localhost:8080/actuator/health

# Check Order Service
curl http://localhost:8081/orders/ping

# Check Config Server
curl http://localhost:8888/actuator/health

# Check Eureka
curl http://localhost:8761/eureka/status
```

## 👨‍💻 Development Guide

### Local Development Setup

For developing without Docker:

```bash
# Start MongoDB locally
# macOS
brew services start mongodb-community

# Windows (if installed)
mongod

# Start Zookeeper and Kafka (if needed for local testing)
# Or omit Kafka features and test single services
```

### Running Services Individually

```bash
# Terminal 1: Eureka Server
cd eureka-server
mvn spring-boot:run

# Terminal 2: Config Server
cd config-server
mvn spring-boot:run

# Terminal 3: Order Service
cd order-service
SPRING_PROFILES_ACTIVE=default mvn spring-boot:run

# Terminal 4: Payment Service
cd payment-service
SPRING_PROFILES_ACTIVE=default mvn spring-boot:run

# And so on...
```

### Debugging

#### IntelliJ IDEA
1. Open project in IntelliJ
2. Right-click service folder → "Run" → "Edit Configurations"
3. Select "Spring Boot"
4. Set environment variables: `SPRING_PROFILES_ACTIVE=default`
5. Click Debug

#### VS Code
1. Install "Extension Pack for Java"
2. Press `F5` or use Run/Debug panel
3. Select "Java" as debug environment

### Code Structure

Each microservice follows this structure:

```
service/
├── src/main/java/com/ecommerce/{service}/
│   ├── controller/           # REST endpoints
│   ├── service/              # Business logic
│   ├── repository/           # Database access
│   ├── entity/               # Domain models
│   ├── dto/                  # Data transfer objects
│   ├── event/                # Kafka events
│   ├── listener/             # Event listeners
│   └── {Service}Application.java  # Main class
├── src/main/resources/
│   ├── application.yml       # Configuration
│   └── bootstrap.yml         # Spring Cloud Config
└── pom.xml                   # Maven dependencies
```

### Adding a New Kafka Event

1. Create event class in `event/` package
2. Create listener class in `listener/` package
3. Configure topic in `application.yml`
4. Publish event from service class

Example:

```java
// src/main/java/com/ecommerce/order/event/OrderEvent.java
@Data
@AllArgsConstructor
public class OrderEvent {
    private String orderId;
    private String status;
    private LocalDateTime timestamp;
}

// src/main/java/com/ecommerce/order/listener/PaymentEventListener.java
@Component
@Slf4j
public class PaymentEventListener {
    @KafkaListener(topics = "payment-events")
    public void handlePaymentEvent(PaymentEvent event) {
        // Handle payment completion
        log.info("Payment received for order: {}", event.getOrderId());
    }
}
```

### Testing

```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=OrderServiceTest

# Run with coverage
mvn test jacoco:report
```

## 📦 Dependencies

Key Spring Cloud dependencies:

- `spring-cloud-starter-config` - Config Server support
- `spring-cloud-starter-netflix-eureka-server` - Service discovery
- `spring-cloud-starter-netflix-eureka-client` - Eureka client
- `spring-cloud-starter-gateway` - API Gateway
- `spring-kafka` - Kafka messaging
- `spring-boot-starter-data-mongodb` - MongoDB integration
- `micrometer-tracing-bridge-brave` - Distributed tracing
- `spring-boot-starter-actuator` - Health checks & metrics

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit changes: `git commit -am 'Add new feature'`
4. Push to branch: `git push origin feature/my-feature`
5. Submit pull request

## 📝 Notes

- All services use MongoDB for document storage
- Kafka ensures asynchronous communication between services
- Each service is independently scalable
- Config Server enables dynamic configuration updates
- Zipkin provides end-to-end request tracing
- Docker Compose simplifies local development

## 📚 Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Kafka Documentation](https://kafka.apache.org/documentation/)
- [MongoDB Documentation](https://docs.mongodb.com/)
- [Docker Documentation](https://docs.docker.com/)
- [Saga Pattern](https://microservices.io/patterns/data/saga.html)

## 📄 License

This project is provided as-is for educational and demonstration purposes.

---

**Last Updated**: March 1, 2026
