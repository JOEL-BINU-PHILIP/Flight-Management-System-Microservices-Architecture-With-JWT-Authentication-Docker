
# Flight Management System — Microservices Architecture

A Flight Booking System built using **Spring Boot Microservices**, **MongoDB**, **Eureka**, **API Gateway**, **Config Server**, **RabbitMQ**, **Auth Service with JWT**, and **Docker**.

---

## Project Summary Report

 **[Project Summary Report & Important Screenshots(PDF)](https://github.com/JOEL-BINU-PHILIP/Flight-Management-System-Microservices-Architecture-With-JWT-Authentication-Docker/blob/main/ProjectSummaryReport.pdf)**

---

## Microservices Included

* **Auth Service** (JWT Authentication & Role-based access)
* **Flight Service** (Manage flights & airlines)
* **Booking Service** (Book flights, PNR generation, history)
* **Email Service** (RabbitMQ listener → sends emails)
* **API Gateway** (JWT filtering & routing)
* **Eureka Service Registry**
* **Config Server (Git-backed)**

---

## Authentication

Implemented using a dedicated **Auth Service**:

* User Registration
* User Login
* JWT generation
* Token validation through API Gateway
* Propagation using `X-USER-EMAIL` & `X-USER-ROLE` headers

---

## Docker & Docker Compose

The entire system runs in containers using:

* **Dockerfiles** for each microservice
* **Docker Compose** for orchestrating:

  * Config Server
  * Eureka
  * API Gateway
  * Auth, Flight, Booking, Email Services
  * MongoDB
  * RabbitMQ

Run everything with:

```bash
docker-compose up --build
```

---

## Features

* Search available flights
* View flight details
* Book tickets (multi-passenger)
* PNR generation & ticket retrieval
* Booking history
* Email notifications on booking/cancellation
* Centralized config management
* Load-balanced routing through Gateway

---

## Technologies Used

* **Java 17**
* **Spring Boot 3**
* **Spring Cloud (Eureka, Gateway, Feign, Config)**
* **MongoDB**
* **RabbitMQ**
* **Docker & Docker Compose**
* **JMeter for Load Testing**
* **SonarCloud for Code Quality**

---

How to Run (Local or Docker)

### **Option 1 — Run via Docker**

```bash
docker-compose up --build
```

### **Option 2 — Run Manually**

1. Start **Config Server**
2. Start **Eureka Server**
3. Start **API Gateway**
4. Start **Auth Service**
5. Start **Flight Service**, **Booking Service**, **Email Service**

---

Load Testing

Performed using **JMeter CLI** for:

* 20 users
* 50 users
* 100 users

