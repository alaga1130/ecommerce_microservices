# Ecommerce Saga Choreography Microservices Demo

## Architecture Overview
- 4 Microservices: order-service, payment-service, inventory-service, shipping-service
- Async + Sync communication via Kafka and REST
- Kafka for event choreography
- MongoDB for each service
- Zipkin for distributed tracing
- Spring Boot 3 + Java 17
- Docker & Docker Compose
- Eureka Server for service discovery
- Config Server (native filesystem mode)
- API Gateway (Spring Cloud Gateway)
- Mongo Express for DB visualization

## Step-by-Step Setup Guide

### Prerequisites
- Java 17
- Docker & Docker Compose
- Git Bash or PowerShell
- Postman

### Build All Services
1. Open terminal in project root
2. Build each service:
   - `cd order-service && mvn clean package`
   - `cd payment-service && mvn clean package`
   - `cd inventory-service && mvn clean package`
   - `cd shipping-service && mvn clean package`
   - `cd eureka-server && mvn clean package`
   - `cd config-server && mvn clean package`
   - `cd api-gateway && mvn clean package`

### Start the Environment
1. In project root, run:
   - `docker-compose up --build`

### Access Services
- API Gateway: [http://localhost:8080](http://localhost:8080)
- Eureka: [http://localhost:8761](http://localhost:8761)
- Zipkin: [http://localhost:9411](http://localhost:9411)
- Mongo Express:
   - Order: [http://localhost:8081](http://localhost:8081)
  - Payment: [http://localhost:8082](http://localhost:8082)
  - Inventory: [http://localhost:8083](http://localhost:8083)
  - Shipping: [http://localhost:8084](http://localhost:8084)

### Using Postman
- Import `PostmanCollection.json` into Postman
- Use the provided requests to test each service via API Gateway

### Using Zipkin
- Open Zipkin UI ([http://localhost:9411](http://localhost:9411))
- Send requests via Postman
- View traces for distributed requests across microservices

### Visualizing MongoDB Data
- Open Mongo Express for each service
- View and manage data in each microservice's MongoDB

### Customization
- Kafka topics, MongoDB URIs, ports, and service names are set in each service's `application.yml`
- Docker Compose manages all containers and networks

## Source Code
- Each service is in its own folder with Maven, Dockerfile, and Spring Boot code
- Saga choreography is implemented via Kafka event listeners

## Troubleshooting
- Ensure all ports are free before starting
- If containers fail, check logs with `docker-compose logs <service>`
- For build errors, check Maven output

---

For further customization or advanced saga logic, edit the event listener classes in each microservice.
