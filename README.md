# Distributed E-commerce System with Spring Cloud

## Project Overview

The **Distributed E-commerce System** is built using microservice architecture and Spring Cloud. The system involves several microservices responsible for different aspects of an e-commerce application, including:

- **Product Service**
- **Order Service**
- **Customer Service**
- **Inventory Service**
- **Payment Service**

This system leverages Spring Cloud features such as service discovery, API Gateway, load balancing, circuit breaker, centralized configuration, distributed transactions, resilience patterns, and security.

The system is fully **dockerized** using Docker Compose to make deployment and management easier.

## Technologies Used

- **Spring Boot**
- **Spring Cloud (Eureka, Gateway, Config, Circuit Breaker, Load Balancer)**
- **Spring Data JPA**
- **H2 Database**
- **Spring Security (OAuth2/JWT)**
- **Spring Boot Actuator**
- **Spring Cloud Config Server**
- **Liquibase**
- **Docker and Docker Compose**

## Microservices Overview

1. **Product Service**: Manages product details such as id, name, price, and quantity.
2. **Order Service**: Handles order and order item entities, including the customer, order date, and items in the order.
3. **Customer Service**: Manages customer information, including first name, last name, and email.
4. **Inventory Service**: Manages inventory stock, including product id and available quantity.
5. **Payment Service**: Handles payment transactions, including the order id, amount, and status.


### Each service contains:

- A `Dockerfile` for containerizing the service.
- A `Liquibase` changelog for setting up the database schema.
- Spring Cloud dependencies for service discovery, configuration management, and resilience patterns.

## Features

### Project Setup and Microservice Creation
- Separate Spring Boot projects for each service.
- Entity definitions for each service.
- Database configuration using H2.
- Liquibase setup for schema management.

### Service Discovery and API Gateway
- Eureka Server for service discovery.
- API Gateway (Spring Cloud Gateway) for routing requests to microservices.

### Centralized Configuration and Load Balancing
- Spring Cloud Config Server for centralized configuration management.
- Client-side load balancing using Spring Cloud LoadBalancer.

### Circuit Breaker and Resilience
- Circuit breaker pattern using Spring Cloud Circuit Breaker.
- Fallback methods, retries, and bulkhead patterns for service-to-service calls.

### Security and Monitoring
- OAuth2/JWT token-based security.
- Centralized logging using ELK stack or other logging solutions.
- Monitoring and metrics collection using Spring Boot Actuator, Prometheus, and Grafana.

## Getting Started

### Prerequisites

- **Docker**: Ensure that Docker and Docker Compose are installed on your system.
- **JDK 11 or higher**: Required for running the Spring Boot applications.

### Clone the Repository

git clone https://github.com/milamilovic/InventoryCloudManagementSystem.git
cd InventoryCloudManagementSystem

### Running with Docker Compose

1. **Build and Start Services**: From the root directory of the project, run:

    ```bash
    docker-compose up --build
    ```

    This will start all the microservices, Eureka Server, API Gateway, and Config Server.

2. **Access the Services**:

    - Eureka Server: [http://localhost:8761](http://localhost:8761)
    - API Gateway: [http://localhost:8085](http://localhost:8085) (Routing API)
    - Product Service: [http://localhost:8084](http://localhost:8084)
    - Order Service: [http://localhost:8082](http://localhost:8082)
    - Customer Service: [http://localhost:8080](http://localhost:8080)
    - Inventory Service: [http://localhost:8081](http://localhost:8081)
    - Payment Service: [http://localhost:8083](http://localhost:8083)

### Docker Compose Configuration

The `docker-compose.yml` file contains all necessary services, including:

- **Eureka Server** for service discovery.
- **Spring Cloud Config Server** for centralized configuration management.
- **Spring Cloud Gateway** for routing.
- **All microservices**: product-service, order-service, customer-service, inventory-service, and payment-service.

### Microservice Ports

| Microservice         | Port     |
|----------------------|----------|
| Eureka Server        | 8761     |
| API Gateway          | 8085     |
| Product Service      | 8084     |
| Order Service        | 8082     |
| Customer Service     | 8080     |
| Inventory Service    | 8081     |
| Payment Service      | 8083     |
| Grafana              | 3000     |
| Prometheus           | 9090     |

### Database

- The system uses an **H2 Database** for simplicity.
- Liquibase is used to manage database schema migrations.

### Security

- **OAuth2/JWT** tokens are used to secure the API Gateway and individual services.
- Ensure to configure your authentication before accessing secure endpoints.

### Monitoring and Logging

- **Spring Boot Actuator** is used for metrics collection.
- Logs are centralized via the ELK stack (Elasticsearch, Logstash, and Kibana) or any alternative logging solution.

## Troubleshooting

- **Service not starting**: Check if all Docker containers are running correctly with `docker ps`. Look at logs for detailed errors (`docker-compose logs`).
- **JWT Token issues**: Make sure the authentication service is properly configured and JWT tokens are being passed in the request headers.
- **401 oe 403 between services**:
    - Do `docker exec -it elasticsearch bin/elasticsearch-reset-password -u elastic` to change the elasticsearch password
    - Put that password in logstash-sample.config file and logstash.yml file and use it to login to Kibana later too
    - do mvn clean package for all microservices
    - Do commands `docker-compose down` `docker-compose build` and `docker-compose up --force-recreate -d`
    - if Kibana is not working do this command:
        Invoke-WebRequest -Uri "http://localhost:9200/_security/service/elastic/kibana/credential/token/token1?pretty" `
        -Method Post `
        -Headers @{Authorization = "Basic $( [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes('elastic:8s*GtRRLmX_XP0htMS7s')))"}` 

## Monitoring queries

- Add data source http://prometheus:9090
- dashboard panel add
- use queries:
  *1) total number of requests per service*
  
`http_server_requests_seconds_count{job="spring-microservices", instance=~"product-service:8087|order-service:8087|customer-service:8087|inventory-service:8087|payment-service:8087"}`

*2) request duration per service*

`rate(http_server_requests_seconds_sum{job="spring-microservices", instance=~"product-service:8087|order-service:8087|customer-service:8087|inventory-service:8087|payment-service:8087"}[1m])
/
rate(http_server_requests_seconds_count{job="spring-microservices", instance=~"product-service:8087|order-service:8087|customer-service:8087|inventory-service:8087|payment-service:8087"}[1m])`

*3) error rates*:

**a) 4-- errors**

`http_server_requests_seconds_count{job="spring-microservices", instance=~"product-service:8087|order-service:8087|customer-service:8087|inventory-service:8087|payment-service:8087", status=~"4.*"}`

**b) 5-- errors**

`http_server_requests_seconds_count{job="spring-microservices", instance=~"product-service:8087|order-service:8087|customer-service:8087|inventory-service:8087|payment-service:8087", status=~"5.*"}`

*4) percentage of errors per service*
`sum(rate(http_server_requests_seconds_count{job="spring-microservices", instance=~"product-service:8087|order-service:8087|customer-service:8087|inventory-service:8087|payment-service:8087", status=~"4.*"}[1m])) 
+ 
sum(rate(http_server_requests_seconds_count{job="spring-microservices", instance=~"product-service:8087|order-service:8087|customer-service:8087|inventory-service:8087|payment-service:8087", status=~"5.*"}[1m]))
/
sum(rate(http_server_requests_seconds_count{job="spring-microservices", instance=~"product-service:8087|order-service:8087|customer-service:8087|inventory-service:8087|payment-service:8087"}[1m]))`


## Author
Mila Milovic
