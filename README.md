# TodoController API

## Overview

The `TodoController` API provides endpoints to manage a list of TODO items. The API supports operations such as creating, reading, updating, and deleting TODO items. The API is secured with role-based access control, where certain operations require admin privileges.

## Endpoints

### Health Check

#### `GET /todo/ping`

- **Description**: Checks if the Todo controller is running.
- **Response**:
    - `200 OK` with message "Todo controller is running..."

### Get All Todos

#### `GET /todo`

- **Description**: Retrieves a list of all TODO items.
- **Response**:
    - `200 OK` with a list of TODO items.
    - `204 No Content` if no TODO items are found.

### Get Todo by ID

#### `GET /todo/{id}`

- **Description**: Retrieves a TODO item by its ID.
- **Path Parameter**: `id` - ID of the TODO item.
- **Response**:
    - `200 OK` with the TODO item.
    - `404 Not Found` if no TODO item with the specified ID is found.

### Add Todo

#### `POST /todo`

- **Description**: Adds a new TODO item.
- **Request Body**: JSON representation of the TODO item.
- **Response**:
    - `200 OK` with the created TODO item.
    - `409 Conflict` if a TODO item with the same ID already exists.
- **Security**: Requires `ADMIN` role.

### Update Todo

#### `PUT /todo`

- **Description**: Updates an existing TODO item.
- **Request Body**: JSON representation of the TODO item.
- **Response**:
    - `200 OK` with the updated TODO item.
    - `404 Not Found` if the TODO item with the specified ID does not exist.
- **Security**: Requires `ADMIN` role.

### Delete Todo

#### `DELETE /todo/{id}`

- **Description**: Deletes a TODO item by its ID.
- **Path Parameter**: `id` - ID of the TODO item.
- **Response**:
    - `200 OK` with a message indicating the TODO item has been deleted.
    - `404 Not Found` if no TODO item with the specified ID is found.
- **Security**: Requires `ADMIN` role.

## Error Handling

- **500 Internal Server Error**: Returned for any server-side errors with a message detailing the issue.

## Example TODO JSON

```json
{
    "id": 1,
    "todoValue": "Sample TODO"
}
```

## Dependencies

- Spring Framework
- Jakarta WS-RS
- Hibernate/JPA
- SLF4J (for logging)

## Setup

1. Ensure you have Java and Maven installed.
2. Clone the repository.
3. Run `mvn clean install` to build the project.
4. Deploy the application to your favorite servlet container.

## Contact

For any questions or issues, please contact [Your Name] at [your.email@example.com].
