# User Registry Service

A Spring Boot application for managing user registration and retrieval, built with Clean Architecture principles.

## Project Structure

The project follows Clean Architecture with three main layers:
- **Domain**: Core business logic and entities
- **Application**: Use cases and service implementation
- **Infrastructure**: External concerns (web, persistence, etc.)

## Prerequisites

- Java 21
- Maven 3.8+
- Git

## Building and Running

### Build
```bash
mvn clean install
```

### Run
The application can be run in different profiles:

#### Default Profile (Development)
```bash
mvn spring-boot:run
```

#### Development Profile
```bash
mvn spring-boot:run -Dspring.profiles.active=dev
```

#### Production Profile
```bash
mvn spring-boot:run -Dspring.profiles.active=prod
```

## Database

The application uses H2 in-memory database for development and testing.

### H2 Console
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `user`
- Password: `password`

## API Documentation

### Endpoints

#### Register User
- **POST** `/api/v1/users`
- **Request Body**:
```json
{
    "username": "johndoe",
    "birthdate": "1990-01-01",
    "country_of_residence": "France",
    "phone_number": "1234567890",      // Optional
    "gender": "Male"                   // Optional (Male/Female/Other)
}
```
- **Response** (201 Created):
```json
{
    "id": 1,
    "username": "johndoe",
    "birthdate": "1990-01-01",
    "country_of_residence": "France",
    "phone_number": "1234567890",
    "gender": "Male"
}
```

#### Get User by ID
- **GET** `/api/v1/users/{id}`
- **Response** (200 OK):
```json
{
    "id": 1,
    "username": "johndoe",
    "birthdate": "1990-01-01",
    "country_of_residence": "France",
    "phone_number": "1234567890",
    "gender": "Male"
}
```

### Validation Rules
- Username: 3-50 characters
- Birthdate: Must be in the past, and user must be 18 years or older
- Country: Must be "France"
- Phone number (optional): 10-15 characters
- Gender (optional): Must be "Male", "Female", or "Other"

### Error Responses
- **400 Bad Request**: Validation errors
- **404 Not Found**: User not found
- **409 Conflict**: Username already exists

## Testing

### Run Unit Tests
```bash
mvn test
```

### Run Integration Tests
```bash
mvn verify
```

### Test Coverage
Generate JaCoCo coverage report:
```bash
mvn clean verify
```
Access the report at: `target/site/jacoco/index.html`

## Configuration Properties Profiles

- Default Profile (application.yml)

- Development Profile (application-dev.yml)

- Production Profile (application-prod.yml)

## Code Quality

### Generate JavaDoc
```bash
mvn javadoc:javadoc
```

## Postman Testing

### Collection Setup
The Postman collection for testing the API is available in the `/postman` directory:
```bash
/postman/User_Registry_API.postman_collection.json
```

### Import Collection
1. Open Postman
2. Click "Import" button
3. Choose "File" > "Upload Files"
4. Select the `User_Registry_API.postman_collection.json` file
5. Click "Import"

### Available Test Cases

#### Register User Endpoints
- **Success Cases**
    - Register user with all valid fields
    - Register user with only required fields

- **Validation Error Cases**
    - Missing required fields (username, birthdate, country)
    - Invalid username (less than 3 characters)
    - Future birthdate
    - Invalid country (non-France)
    - Invalid gender value
    - Empty required fields
    - Missing optional fields

#### Get User Endpoints
- **Success Cases**
    - Retrieve existing user
- **Error Cases**
    - Retrieve non-existent user

### Environment Variables
The collection uses the following environment variable:
- `baseUrl`: Default value is `http://localhost:8080`

To set up the environment:
1. In Postman, click the "Environment quick look" button (👁️)
2. Add a new environment
3. Set the variable `baseUrl` to your server URL
4. Select the environment from the dropdown

### Running Tests
1. Make sure the application is running
2. Select the appropriate environment
3. Open the collection runner (Runner button)
4. Select the tests you want to run
5. Click "Run User Registry API"

## Contributing

1. Fork the repository
2. Create a feature branch
3. Create an Issue
4. Create a Pull Request

## That's all folks

Thank you for checking out this project, and happy coding!
