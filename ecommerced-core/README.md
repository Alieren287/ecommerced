# Ecommerce Core Module

This is the core module of the ecommerce platform built using hexagonal architecture. It provides fundamental utilities
and components that can be used across all other modules without any framework dependencies.

## Architecture Overview

The core module follows clean architecture principles and provides:

- **Exception Handling Framework**: Structured exception handling with error codes and parameterized messages
- **Base Response Structure**: Standardized API responses with Lombok annotations
- **Use Case Pattern**: Implementation of business logic through use cases
- **Base Controller**: Utility methods for response creation
- **Logging Utilities**: Centralized logging with MDC support

## Components

### 1. Exception Framework (`com.alier.core.exception`)

#### ErrorCode Interface

```java
public interface ErrorCode {
    String getCode();

    String getMessage();

    int getHttpStatusCode();
}
```

#### Base Exception Class

All exceptions extend `BaseException` which provides:

- `ErrorCode errorCode`: The error code with message and HTTP status
- `Object[] args`: **Parameters for dynamic error messages**

**Why the `args` field?**
The `args` field enables **parameterized error messages** for better flexibility:

```java
// Example error code with placeholders
USER_NOT_FOUND("USER_001","User with ID {0} not found in {1}",404)

// Usage with parameters
throw new

ResourceNotFoundException(USER_NOT_FOUND, userId, "database");
// Results in: "User with ID 123 not found in database"
```

Benefits:

- **Internationalization**: Translate messages while preserving parameters
- **Dynamic content**: Runtime values in error messages
- **Structured logging**: Parameters logged separately for analysis
- **Reusable error codes**: Same code with different contexts

#### Exception Classes

- `ValidationException`: For validation errors (400)
- `ConflictException`: For business logic conflicts (409)
- `ResourceNotFoundException`: For missing resources (404)
- `InternalServerException`: For internal server errors (500)

All exceptions are now clean and concise thanks to inheritance from `BaseException`.

#### Usage Example

```java
public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND("USER_001", "User with ID {0} not found", 404),
    INVALID_EMAIL("USER_002", "Invalid email format: {0}", 400),
    DUPLICATE_USERNAME("USER_003", "Username '{0}' already exists", 409);

    // implementation...
}

// Usage with parameters
throw new

ResourceNotFoundException(UserErrorCode.USER_NOT_FOUND, userId);
throw new

ValidationException(UserErrorCode.INVALID_EMAIL, email);
throw new

ConflictException(UserErrorCode.DUPLICATE_USERNAME, username);
```

### 2. Base Response (`com.alier.core.response`)

**Using Lombok annotations for clean code:**

```java

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private List<ErrorDetail> errors;
    private LocalDateTime timestamp;
    private String requestId;

    // Only constructors and factory methods, no boilerplate getters/setters
}
```

The `BaseResponse<T>` class provides a standardized response structure:

```java
{
        "success":true,
        "message":"Success",
        "data":{...},
        "errors":null,
        "timestamp":"2024-01-01T10:00:00",
        "requestId":"req-123"
        }
```

#### Usage Example

```java
// Success responses
return BaseResponse.success(data);
return BaseResponse.

success("Custom message",data);

// Error responses
return BaseResponse.

error("Error message");
return BaseResponse.

error("Error message",errorDetails);
```

### 3. Use Case Pattern (`com.alier.core.usecase`)

#### UseCase Interface

```java
public interface UseCase<INPUT, OUTPUT> {
    OUTPUT execute(INPUT input);
}
```

#### UseCaseHandler

The `UseCaseHandler` provides centralized execution of use cases:

```java
public class UseCaseHandler {
    public <INPUT, OUTPUT> OUTPUT execute(UseCase<INPUT, OUTPUT> useCase, INPUT input);

    public <INPUT, OUTPUT> CompletableFuture<OUTPUT> executeAsync(UseCase<INPUT, OUTPUT> useCase, INPUT input);
}
```

#### Usage Example

```java
public class CreateUserUseCase implements UseCase<CreateUserRequest, CreateUserResponse> {

    @Override
    public CreateUserResponse execute(CreateUserRequest input) {
        // Validation with parameterized messages
        if (input.getEmail() == null) {
            throw new ValidationException(UserErrorCode.INVALID_EMAIL, "null");
        }

        // Business logic here
        return new CreateUserResponse();
    }
}
```

### 4. Base Controller (`com.alier.core.controller`)

The `BaseController` provides utility methods for creating responses:

```java
public class UserController extends BaseController {

    private final UseCaseHandler useCaseHandler = new UseCaseHandler();
    private final CreateUserUseCase createUserUseCase = new CreateUserUseCase();

    public BaseResponse<CreateUserResponse> createUser(CreateUserRequest request) {
        try {
            CreateUserResponse result = useCaseHandler.execute(createUserUseCase, request);
            return success("User created successfully", result);
        } catch (ValidationException e) {
            return error("Validation failed: " + e.getMessage());
        } catch (Exception e) {
            return error("Failed to create user: " + e.getMessage());
        }
    }
}
```

### 5. Logging Utilities (`com.alier.core.logging`)

The `LoggerUtil` class provides centralized logging functionality with MDC support:

```java
// Set context
LoggerUtil.setRequestId("req-123");
LoggerUtil.

setUserId("user-456");
LoggerUtil.

setOperation("createUser");

// Log method entry/exit
LoggerUtil.

logMethodEntry(logger, "createUser",request);
LoggerUtil.

logMethodExit(logger, "createUser",response);

// Clear context
LoggerUtil.

clearMDC();
```

## Dependencies

The core module has minimal dependencies:

- **Lombok**: For reducing boilerplate code (getters, setters, constructors)
- **SLF4J**: For logging abstraction
- **Logback**: For logging implementation
- **Jackson**: For JSON processing
- **JUnit Jupiter**: For testing (test scope)

No framework dependencies (Spring, etc.) are included to keep the core module lightweight and reusable.

## Lombok Benefits

With Lombok annotations, our classes are now much cleaner:

**Before:**

```java
public class ExampleRequest {
    private Long id;
    private String name;

    public ExampleRequest() {
    }

    public ExampleRequest(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

**After:**

```java

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExampleRequest {
    private Long id;
    private String name;
}
```

## Example Usage

See the `com.alier.core.example` package for complete examples:

### Running Examples

```java
// Create instances
ExampleController controller = new ExampleController();
ExampleRequest request = new ExampleRequest(1L, "Test", "Description");

// Process request
BaseResponse<ExampleResponse> response = controller.processExample(request);

// Handle response
if(response.

isSuccess()){
ExampleResponse data = response.getData();
// Handle success
}else{
String error = response.getMessage();
// Handle error
}
```

## Best Practices

1. **Error Handling**: Create your own `ErrorCode` enums with parameterized messages
2. **Lombok Usage**: Use `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor` for DTOs
3. **Logging**: Use LoggerUtil for consistent logging with context
4. **Use Cases**: Implement business logic in use cases, not in controllers
5. **Response Structure**: Always return responses using BaseResponse for consistency
6. **Dependencies**: Keep this module free of framework dependencies

## Integration with Web Frameworks

This core module can be easily integrated with any web framework:

### Spring Boot Integration

```java

@RestController
@RequestMapping("/api/users")
public class UserController extends BaseController {

    private final UseCaseHandler useCaseHandler = new UseCaseHandler();

    @PostMapping
    public ResponseEntity<BaseResponse<CreateUserResponse>> createUser(@RequestBody CreateUserRequest request) {
        BaseResponse<CreateUserResponse> response = // use core utilities
        return ResponseEntity.ok(response);
    }
}
```

### Other Frameworks

The utilities can be used with any Java web framework by wrapping the BaseResponse in the framework's response format.

## Future Enhancements

This core module can be extended with:

- Caching utilities
- Event publishing/subscribing
- Metrics and monitoring utilities
- Configuration management
- Security utilities 