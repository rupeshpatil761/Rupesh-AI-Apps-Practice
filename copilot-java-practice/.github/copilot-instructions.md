# Java Project Coding Standards

## Technology Stack
- Java version: 21
- Framework: Spring Boot 3.x
- Build System: Maven
- Database: MySQL with Spring Data JPA

## Architecture & Coding Styles
- Always prefer modern Java features like Records for DTOs, text blocks for multiline strings, and pattern matching for switch statements.
- Use constructor injection instead of `@Autowired` field injection for Spring beans.
- Prefer explicit, declarative variable names instead of generic shorthand (e.g., use `customerRepository` instead of `repo`).

## Testing & Validation
- Write unit tests using JUnit 5 and AssertJ for assertions.
- Use Mockito for mocking dependencies.
- Ensure all input parameters in controller endpoints are validated using `jakarta.validation`.