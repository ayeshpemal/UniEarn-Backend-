# UniEarn-Backend-

A platform connecting university students with part-time jobs. Students can register, search by category and location, and apply directly. Employers can post jobs, manage applications, and provide feedback. Features include secure login, job filtering, and a feedback/rating system to enhance trust.

## Installation and Setup

Follow these steps to get the project running locally:

### 1. Clone the Repository

```bash
git clone https://github.com/ayeshpemal/UniEarn-Backend-.git
cd UniEarn-Backend-
```

### 2. Setup database

 - Install PostgreSQL.<br />
 - Change credentials in properties as below.
```bash
spring.datasource.username=your_username
spring.datasource.password=your_password
```
or
Create user account with given credentials.<br />
 - Create database "uniearn".

### 3. Start the Development Server

Run the project using IDE.
The backend server will start on http://localhost:8100

### 4. API Documentation

Once the backend is running, you can access the Swagger API documentation at:

```bash
http://localhost:8100/swagger-ui/index.html#/
```
