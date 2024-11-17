# Transaction Verification & Reconciliation System

## Overview

The **Transaction Verification & Reconciliation System** is a Spring Boot application designed to handle the verification, reconciliation, and management of transactions in a financial system. It ensures that all transactions are validated, reconciled, and compared with external data sources, such as stock prices from the Yahoo Finance API. The system provides role-based access control, enabling users with different levels of permissions to access and modify transaction data.

This project allows **junior** and **senior** users to manage transactions, handle mismatches, and perform reconciliation tasks. Senior users have additional privileges to view and compare data against external sources, retrieve stock information, and generate statistics.

## Features

- **Transaction Management**: Create, update, view, and delete transactions. 
- **Reconciliation**: Verify transactions, detect mismatches, and compare with external sources.
- **Stock Data Integration**: Fetch stock information from Yahoo Finance API and compare it with transaction data.
- **Role-Based Access Control**: Different levels of access for junior and senior users.
- **Alert System**: Generate alerts for mismatches and transaction-related issues.
- **Statistics**: View statistics on transactions, mismatches, and senior user-specific data.
- **Mismatch Summary**: Generate a summary of all detected mismatches.

## Technologies

- **Spring Boot**: The main framework for building the application.
- **Spring Security**: For implementing authentication and role-based authorization.
- **JPA (Java Persistence API)**: For interacting with the MySQL database.
- **Thymeleaf**: For rendering HTML views.
- **MySQL**: For the database that stores transaction and mismatch logs.
- **Yahoo Finance API**: For fetching real-time stock data.
- **Postman**: For testing and interacting with the API.

## Endpoints

### Authentication

**POST** `http://localhost:8080/api/authenticate`

Authenticate the user. This endpoint provides a success message for authentication.

#### Request Body

```json
{
    "transactionId": "TXN123",
    "uid": "UID456",
    "price": 50.0,
    "quantity": 10,
    "status": "PENDING",
    "assetClass": null,
    "strategy": null,
    "symbol": "AAPL"
}
```

---

### Transaction Management

- **POST** `http://localhost:8080/api/transactions`: Create a new transaction.
- **GET** `http://localhost:8080/api/transactions`: Retrieve all transactions.
- **PUT** `http://localhost:8080/api/transactions/{transactionId}`: Update an existing transaction by transaction ID.
- **DELETE** `http://localhost:8080/api/transactions/{transactionId}`: Delete a transaction by transaction ID.

#### Example POST request:

```json
{
    "transactionId": "TXN123",
    "uid": "UID456",
    "price": 50.0,
    "quantity": 10,
    "status": "PENDING",
    "assetClass": null,
    "strategy": null,
    "symbol": "AAPL"
}
```

---

### Reconciliation

- **POST** `http://localhost:8080/api/reconciliation/verify`: Verify transactions by comparing internal and external data sources.
  
  #### Example request body:

```json
[
  {
    "transactionId": "TXN001",
    "price": 100.5,
    "quantity": 10,
    "uid": "user123",
    "status": "COMPLETED",
    "assetClass": null,
    "strategy": null,
    "symbol": null,
    "transactionStatusEnum": "COMPLETED"
  }
]
```

- **GET** `http://localhost:8080/api/reconciliation/active-options`: Retrieve the most active options from Yahoo Finance (only accessible by senior users).
- **GET** `http://localhost:8080/api/reconciliation/mismatches`: Retrieve a list of all mismatches detected in transactions (only accessible by senior users).
- **GET** `http://localhost:8080/api/reconciliation/mismatches/{transactionId}`: Retrieve mismatches for a specific transaction by its ID (only accessible by senior users).
- **GET** `http://localhost:8080/api/reconciliation/mismatch-summary`: Retrieve a summary of all detected mismatches.

---

### Alerts

- **GET** `http://localhost:8080/api/alerts/admin`: Retrieve admin-level alerts, including all critical mismatches and issues.
- **GET** `http://localhost:8080/api/alerts/transaction/{transactionId}`: Retrieve alerts for a specific transaction.

---

### Statistics

- **GET** `http://localhost:8080/api/statistics/transactions`: Retrieve statistics on all transactions by their status.
- **GET** `http://localhost:8080/api/statistics/mismatches`: Retrieve the total number of mismatches detected across all transactions.
- **GET** `http://localhost:8080/api/statistics/senior`: Retrieve statistics specifically for senior users.
- **GET** `http://localhost:8080/api/statistics/mismatches-by-field`: Retrieve statistics of mismatches grouped by field.
- **GET** `http://localhost:8080/api/statistics/transactions-with-mismatch-counts`: Retrieve a list of all transactions with their associated mismatch counts.

---

### Stock Data

- **GET** `http://localhost:8080/api/stocks/info/{symbol}`: Retrieve information about a specific stock symbol (only accessible by senior users).

---

## Database Schema

The system uses a MySQL database to store transactions and mismatch logs. The following tables are used:

### Transactions Table
- `transactionId` (Primary Key)
- `uid` (User ID)
- `price`
- `quantity`
- `status` (Enum: `PENDING`, `REJECTED`, `COMPLETED`)
- `assetClass`
- `strategy`
- `symbol`

### MismatchLogs Table
- `id` (Primary Key)
- `transactionId` (Foreign Key to Transactions)
- `field`
- `internalValue`
- `externalValue`
- `source`
- `description`
- `timestamp`

---

## Security

The application uses Spring Security for role-based access control. There are two main roles:

1. **Junior**: Can create and view transactions, but has limited access to other features.
2. **Senior**: Has full access to transactions, reconciliation, and statistics, and can compare transactions with external data sources.

### Security Configuration

- **CSRF** is disabled for simplicity.
- The `/api/authenticate` endpoint is publicly accessible.
- Role-based access control is implemented using annotations like `@PreAuthorize("hasRole('SENIOR')")` for sensitive endpoints.

---

## How to Run the Project

### Prerequisites

- Java 11 or higher
- Maven
- MySQL database

### Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/transaction-verification-reconciliation-system.git
   ```

2. Navigate to the project directory:
   ```bash
   cd transaction-verification-reconciliation-system
   ```

3. Update the `application.properties` file with your MySQL database credentials.

4. Run the project using Maven:
   ```bash
   mvn spring-boot:run
   ```

5. Access the API at `http://localhost:8080`.

---

## Testing the API

You can use **Postman** or any HTTP client to test the API endpoints.

1. **Authentication**: Use the `/api/authenticate` endpoint to authenticate users.
2. **Transaction Management**: Use the `/api/transactions` endpoints to create, update, view, and delete transactions.
3. **Reconciliation**: Use the `/api/reconciliation` endpoints to verify transactions and view mismatch summaries.
4. **Alerts**: Retrieve alerts for specific transactions or admin-level alerts using the `/api/alerts` endpoints.
5. **Statistics**: View various statistics on transactions and mismatches using the `/api/statistics` endpoints.
6. **Stock Data**: Retrieve stock data for specific symbols using the `/api/stocks` endpoint.

