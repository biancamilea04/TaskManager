# TaskManager

A web-based task management application built with Spring Boot that allows users to manage their personal tasks with authentication and secure access control.

## Overview

TaskManager is a full-stack web application that enables registered users to create, update, delete, and track their personal tasks. The application features a secure authentication system, user profile management, and a responsive web interface built with Thymeleaf templates and modern CSS/JavaScript.

## Features

### Core Functionality
- **User Authentication & Registration**: Secure user registration and login system with Spring Security
- **Task Management**: Complete CRUD operations for personal tasks
  - Create new tasks with title, description, due date, estimated hours, and status
  - Update existing tasks
  - Delete tasks
  - View all personal tasks
- **User Profile Management**: Manage user profile information and account details
- **Session Management**: Cookie-based session handling for authenticated users
- **Responsive UI**: Modern web interface with CSS styling and JavaScript interactivity

### Task Features
- **Task Properties**:
  - Title and description
  - Due date tracking
  - Activity hours estimation
  - Status management
  - Unique task numbering per user
- **User-specific Tasks**: Each user can only view and manage their own tasks
- **RESTful API**: Complete REST API endpoints for task operations

### Security Features
- Spring Security integration
- Password encryption
- Session-based authentication
- Protected API endpoints
- User authorization checks

## Technologies

### Backend
- **Java 17**: Primary programming language
- **Spring Boot 3.4.5**: Main framework for application development
- **Spring Security 6.4.5**: Authentication and authorization
- **Spring Data JPA**: Database operations and ORM
- **Hibernate**: JPA implementation for database mapping
- **Lombok**: Reducing boilerplate code

### Frontend
- **Thymeleaf**: Server-side template engine
- **HTML5/CSS3**: Modern web standards
- **JavaScript**: Client-side interactivity
- **HTMX**: Enhanced HTML interactions

### Database
- **Oracle Database**: Primary database with JDBC connectivity
- **JPA/Hibernate**: Object-relational mapping

### Development Tools
- **Maven**: Build automation and dependency management
- **Spring Boot DevTools**: Development-time features
- **Spring Boot Test**: Testing framework

## Project Structure

```
src/
├── main/
│   ├── java/org/example/projectjava/
│   │   ├── Controller/           # REST controllers
│   │   │   ├── TasksController.java
│   │   │   ├── LoginRegisterController.java
│   │   │   ├── ProfileController.java
│   │   │   └── HomeController.java
│   │   ├── Model/               # Data models and repositories
│   │   │   ├── Task/
│   │   │   ├── Member/
│   │   │   └── MemberDetails/
│   │   ├── Com/                 # Request/Response DTOs
│   │   ├── Security/            # Security configuration
│   │   └── ProjectJavaApplication.java
│   └── resources/
│       ├── templates/           # Thymeleaf templates
│       ├── static/             # CSS, JS, and static assets
│       └── application.properties
```

## Database Schema

The application uses the following main entities:
- **MEMBERS**: User accounts with authentication details
- **MEMBER_DETAILS**: Extended user profile information
- **TASKS**: User tasks with all task-related properties
- **DEPARTMENTS**: Organizational structure (future feature)
- **PROJECTS**: Project management (future feature)

## API Endpoints

### Task Management
- `GET /api/tasks` - Retrieve all tasks for authenticated user
- `POST /api/tasks` - Create a new task
- `PUT /api/tasks/{memberTaskNumber}` - Update an existing task
- `DELETE /api/tasks/{memberTaskNumber}` - Delete a task

### Authentication
- `/loginPage` - User login page
- `/registerPage` - User registration page
- `/logout` - User logout

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Oracle Database (with XE instance running on localhost:1521)
- Database user: `taskmanager` with password: `parola`

## Installation & Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd TaskManager
   ```

2. **Database Setup**
   - Ensure Oracle Database is running
   - Create database user `taskmanager` with password `parola`
   - Run the SQL scripts from `project.sql` to create the required tables

3. **Configure Database Connection**
   - Update `src/main/resources/application.properties` if needed
   - Default configuration connects to `localhost:1521:XE`

4. **Build the Project**
   ```bash
   mvn clean install
   ```

5. **Run the Application**
   ```bash
   mvn spring-boot:run
   ```

6. **Access the Application**
   - Open your browser and navigate to `http://localhost:8081`
   - Register a new account or use existing credentials

## Configuration

Key configuration properties in `application.properties`:
- Server runs on port `8081`
- Oracle database connection configured
- Thymeleaf template engine configured
- JPA/Hibernate settings for automatic schema updates

## Development

### Running in Development Mode
The application includes Spring Boot DevTools for hot reloading during development.

### Testing
Run tests using:
```bash
mvn test
```

### Building for Production
```bash
mvn clean package
java -jar target/ProjectJava-0.0.1-SNAPSHOT.jar
```

## Security

- All API endpoints require authentication
- Passwords are encrypted using Spring Security
- Session management through secure cookies
- CSRF protection enabled
- User data isolation (users can only access their own tasks)

## Future Enhancements

Based on the database schema, the application is designed to support:
- Department management
- Project-based task organization
- Team collaboration features
- Advanced user roles and permissions

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is developed as an educational/demonstration application. 
