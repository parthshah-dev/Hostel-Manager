# HostelEase - PG & Hostel Management Monorepo

Welcome to the **HostelEase PG & Hostel Management System** codebase. This project has been structured into a full-stack monorepo with separate client and server sub-directories.

## Project Structure

```
hostelmanagement/
├── backend/                  # Spring Boot 3 (JDK 25, Maven) Server API
│   ├── src/                  # Java source files and configurations
│   ├── pom.xml               # Maven dependencies and build parameters
│   └── mvnw / mvnw.cmd       # Maven Wrapper executables
├── frontend/                 # React 19 + Vite + Tailwind CSS Web Application
│   ├── src/                  # React views, components and assets
│   ├── package.json          # Node dependencies and script commands
│   ├── vite.config.js        # Vite configurations (includes Tailwind CSS v4)
│   └── .env                  # Frontend API environment variables
├── README.md                 # Project orchestration manual
└── .gitignore                # Root git ignores
```

---

## Prerequisites

Ensure you have the following installed on your machine:
1. **Java Development Kit (JDK) 25**
2. **Maven 3.9+** (or use the provided Maven Wrapper)
3. **Node.js** (v18.x or above) & **npm**
4. **MySQL Database Server** (running locally on port `3306`)

---

## Installation & Setup

### 1. Database Configuration
By default, the backend connects to a database named `hostel_db` using standard credentials.
Ensure MySQL is running, then log in to your shell and run:
```sql
CREATE DATABASE IF NOT EXISTS hostel_db;
```
If you need to change database credentials, edit [application.properties](file:///e:/hostelmanagement/backend/src/main/resources/application.properties):
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hostel_db
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

### 2. Backend Server Setup
Navigate to the `backend/` folder and build the application:
```bash
cd backend
./mvnw clean compile
```

### 3. Frontend Client Setup
Navigate to the `frontend/` folder and install dependencies:
```bash
cd frontend
npm install
```

---

## Running the Application

Both applications run independently:

### Run Backend API
```bash
cd backend
# Windows command prompt / powershell:
mvnw.cmd spring-boot:run

# Unix terminal:
./mvnw spring-boot:run
```
The server will start at **http://localhost:8080**.

### Run Frontend Client
```bash
cd frontend
npm run dev
```
The client will start at **http://localhost:5173**.

---

## Configuration & CORS

- **CORS Support**: The backend is configured to accept requests from the frontend client origin (`http://localhost:5173`). This configuration is declared in `backend/src/main/resources/application.properties` and integrated inside the security filter chain in [SecurityConfig.java](file:///e:/hostelmanagement/backend/src/main/java/com/example/hostelmanagement/config/SecurityConfig.java).
- **Client Base URL**: The frontend communicates with the backend via Axios using the API endpoint url declared in `frontend/.env`:
  ```env
  VITE_API_URL=http://localhost:8080
  ```
