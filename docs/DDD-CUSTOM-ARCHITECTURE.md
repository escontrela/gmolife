## DDD Custom Architecture + JavaFX Window Management

This document describes a pragmatic Domain-Driven Design (DDD) architecture tailored for a Java application that uses JavaFX for the UI and a layered package layout. The goal is to reproduce the package structure and conventions used in this codebase while remaining generic and reusable for new projects.

All examples use the package root `com.example.project` — swap it for your group's reverse-domain name.

## Checklist of requirements for this guide

- Provide a clear package structure that mirrors the repository layout.
- Show code examples for domain entities, repositories, use-cases, controllers and DTOs.
- Explain JavaFX window management with an example `UiFlowManager`.
- Include bootstrapping / configuration and wiring examples (Spring Boot friendly).
- Describe infrastructure, DB migration and testing recommendations.

## High-level architecture

- Domain: pure business concepts, entities, value objects, domain services, domain events.
- Application: use cases (interactors), commands/DTOs, orchestration, boundary interfaces.
- API / UI: REST endpoints, JavaFX controllers and views (FXML), input/output adapters.
- Infrastructure: persistence implementations (JPA / JDBC), messaging, external integrations.
- Bootstrap / wiring: DI, configuration, app launcher, JavaFX integration with Spring.

## Recommended package structure

com.example.project
- api                (HTTP / CLI / external adapters)
  - user
    - AddUserController.java
    - AddUserRequest.java
    - AddUserResponse.java
- application
  - mcp                (optional Model-Context-Protocol DTOs for external integrations)
    - dto
      - UpdateTaskMcpRequest.java
  - project
    - AddProjectCommand.java
    - AddProjectUseCase.java
  - task
    - AddTaskCommand.java
    - AddTaskUseCase.java
  - service            (application-level services, caches, facades)
  - ui                 (UI-specific application helpers)
- bootstrap           (application startup & wiring)
  - ApplicationConfig.java
  - ApplicationLauncher.java
- domain
  - project
    - Project.java
    - ProjectId.java
  - task
    - Task.java
    - TaskStatus.java
  - common
    - DomainEvent.java
- infrastructure
  - db
    - migration
      - V1__init.sql
  - repository
    - SpringProjectRepository.java
    - SpringTaskRepository.java
- resources
  - application.yml
  - ui
    - main.fxml
    - styles.css

This layout separates behavior by intent and keeps domain code free of framework dependencies.

## Contract examples (DTOs)

Example of a small request DTO used for an external MCP-style protocol:

```java
package com.example.project.application.mcp.dto;

import java.time.Instant;

public record UpdateTaskMcpRequest(
    Long taskId,
    Long projectId,
    Long userId,
    Long priorityId,
    Long tagId,
    String externalCode,
    String title,
    String description,
    Instant startAt,
    Instant endAt) {}
```

Use records for simple immutable DTOs where the JDK version supports it.

## Domain layer

Keep domain objects POJO-like and free of Spring/JavaFX. Use value objects and explicit factories where necessary.

Example `Project` entity (simple illustration):

```java
package com.example.project.domain.project;

import java.util.Objects;

public class Project {
    private final Long id;
    private String name;

    public Project(Long id, String name) {
        this.id = id;
        this.name = Objects.requireNonNull(name);
    }

    public Long id() { return id; }
    public String name() { return name; }

    public void rename(String newName) {
        this.name = Objects.requireNonNull(newName);
    }
}
```

Repository interface (domain port):

```java
package com.example.project.domain.project;

import java.util.Optional;
import java.util.List;

public interface ProjectRepository {
    Project save(Project project);
    Optional<Project> findById(Long id);
    List<Project> findAll();
}
```

Keep the repository interface free of framework annotations. Implementations live in `infrastructure.repository`.

## Application layer: use-cases and commands

Use-cases are thin orchestrators that call domain objects and the ports (repositories, gateways).

Add project command and use-case interface:

```java
package com.example.project.application.project;

public record AddProjectCommand(String name) {}

public interface AddProjectUseCase {
    Long add(AddProjectCommand command);
}
```

Use-case implementation (wiring to repository):

```java
package com.example.project.application.project;

import com.example.project.domain.project.Project;
import com.example.project.domain.project.ProjectRepository;

public class AddProjectService implements AddProjectUseCase {
    private final ProjectRepository repository;

    public AddProjectService(ProjectRepository repository) {
        this.repository = repository;
    }

    @Override
    public Long add(AddProjectCommand command) {
        Project p = new Project(null, command.name());
        Project saved = repository.save(p);
        return saved.id();
    }
}
```

Note: The repository implementation is responsible for generating persistent identifiers.

## API / UI adapters

API controllers adapt incoming requests into Commands and call use-cases. Keep controllers thin.

Example Spring REST controller:

```java
package com.example.project.api.project;

import org.springframework.web.bind.annotation.*;
import com.example.project.application.project.AddProjectUseCase;
import com.example.project.application.project.AddProjectCommand;

@RestController
@RequestMapping("/api/projects")
public class AddProjectController {
    private final AddProjectUseCase useCase;

    public AddProjectController(AddProjectUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public Long add(@RequestBody AddProjectCommand request) {
        return useCase.add(request);
    }
}
```

For JavaFX, controllers are different: they are UI controllers referenced from FXML and should call application use-cases via injected services. Avoid putting business logic in JavaFX controllers.

## JavaFX Window Management: UiFlowManager

This project uses a dedicated window flow manager to centralize navigation and scene management. The manager loads FXMLs, caches loaded scenes, and switches scenes on the primary stage.

Key goals:
- Centralize FXMLLoader usage and controller wiring.
- Support modal dialogs and scene transitions.
- Integrate with DI (Spring) so controllers can get injected beans.

Example minimal `UiFlowManager`:

```java
package com.example.project.application.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UiFlowManager {
    private final Stage primaryStage;
    private final Map<String, Scene> cachedScenes = new HashMap<>();

    public UiFlowManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void show(String fxmlPath, String title) {
        try {
            Scene scene = cachedScenes.computeIfAbsent(fxmlPath, p -> {
                try {
                    Parent root = FXMLLoader.load(getClass().getResource(p));
                    return new Scene(root);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
            primaryStage.show();
        } catch (RuntimeException ex) {
            // handle load failure
            throw ex;
        }
    }

    public void showModal(String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage dialog = new Stage();
            dialog.initOwner(primaryStage);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root));
            dialog.setTitle(title);
            dialog.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
```

Integration notes:
- If you need Spring injection into JavaFX controllers, use a Spring-enabled FXMLLoader (setControllerFactory) in the bootstrapping code.

Example bootstrapping that integrates Spring and JavaFX controllers:

```java
// ApplicationLauncher.java
package com.example.project.bootstrap;

import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ApplicationLauncher extends Application {
    private ApplicationContext context;

    @Override
    public void init() {
        context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
    }

    @Override
    public void start(Stage primaryStage) {
        UiFlowManager ui = context.getBean(UiFlowManager.class);
        // If UiFlowManager needs the stage, you can wire it after context is created.
        ui = new UiFlowManager(primaryStage);
        ui.show("/ui/main.fxml", "Application");
    }

    @Override
    public void stop() {
        ((AnnotationConfigApplicationContext) context).close();
    }
}
```

And a Spring configuration bean that allows controller injection into FXMLLoader:

```java
package com.example.project.bootstrap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    @Bean
    public UiFlowManager uiFlowManager() {
        // Stage must be provided later by the launcher. Keep a constructor that accepts Stage.
        return new UiFlowManager(null);
    }

    // register repositories, use-cases, services here
}
```

To enable `FXMLLoader` to get Spring-created controllers:

```java
FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/main.fxml"));
loader.setControllerFactory(context::getBean);
Parent root = loader.load();
```

## Infrastructure: persistence & migrations

- Keep SQL migrations in `resources/db/migration` (Flyway convention). Example files: `V1__init.sql`, `V2__add_tasks.sql`.
- Use repository implementations under `infrastructure.repository` to implement the domain ports. Keep the implementation details (JPA, JDBC) here.

Example repository implementation using Spring Data JPA (sketch):

```java
package com.example.project.infrastructure.repository;

import org.springframework.stereotype.Repository;
import com.example.project.domain.project.Project;
import com.example.project.domain.project.ProjectRepository;

@Repository
public class SpringProjectRepository implements ProjectRepository {
    private final JpaSpringProjectRepository jpa;

    public SpringProjectRepository(JpaSpringProjectRepository jpa) { this.jpa = jpa; }

    @Override
    public Project save(Project project) {
        // map Domain->Entity, call jpa.save, map back
        return project; // sketch
    }

    // ... implement other methods
}
```

Prefer small, targeted integration tests for the repository implementations.

## Build & dependencies (maven snippet)

Add JavaFX, Spring Boot and test dependencies in your `pom.xml`.

```xml
<dependencies>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
  </dependency>
  <dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-controls</artifactId>
    <version>${javafx.version}</version>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
  </dependency>
  <!-- DB driver, Flyway, testing deps -->
</dependencies>
```

And pick a plugin to bundle JavaFX for desktop deployments (or run with modular JDK options during development).

## Testing strategy

- Domain: pure unit tests; avoid mocking domain objects.
- Application: unit tests for use-cases; mock repository ports.
- Infrastructure: small integration tests with an in-memory DB (H2) and Flyway migrations.
- UI: test JavaFX controllers with TestFX or by separating logic into testable components. Keep business logic out of controllers.

Example unit test sketch for `AddProjectService` (JUnit + Mockito):

```java
@ExtendWith(MockitoExtension.class)
class AddProjectServiceTest {
    @Mock ProjectRepository repository;
    @InjectMocks AddProjectService service;

    @Test void addNewProject() {
        when(repository.save(any())).thenAnswer(inv -> new Project(1L, ((Project)inv.getArgument(0)).name()));
        Long id = service.add(new AddProjectCommand("My Project"));
        assertEquals(1L, id);
    }
}
```

## Edge cases & tradeoffs

- Keep domain pure: avoid framework annotations in domain classes.
- DTOs and commands are simple types — prefer immutability.
- If you need high-performance reads, implement read-models / projections separate from write-domain models.
- Decide early whether to use Spring-managed JavaFX controllers or construct controllers manually; the former is convenient for DI, the latter for pure JavaFX portability.

## Migration and DB

Keep migrations under `resources/db/migration` and use Flyway at startup to keep DB structure deterministic. Example migration file name: `V1__init.sql`.

## Quick start checklist to scaffold a new project

1. Create package root `com.example.project` and the subpackages described earlier.
2. Add domain model classes and repository interfaces under `domain`.
3. Add use-cases (interfaces) under `application` and their implementations in `application.service`.
4. Add Spring configuration beans in `bootstrap` and an `ApplicationLauncher` that starts JavaFX and wires beans into FXML loaders.
5. Add `UiFlowManager` and FXML files under `resources/ui`.
6. Add Flyway migrations to `resources/db/migration` and configure Flyway in `application.yml`.
7. Add tests for domain and use-cases, and an integration test for repository implementations.

## Example `application.yml` (development)

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:dev;DB_CLOSE_DELAY=-1
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: validate
flyway:
  enabled: true
  locations: classpath:db/migration
```

## Conclusion & next steps

This guide provides a practical DDD folder layout and code samples demonstrating how to write domain-pure code, application use-cases, adapters (API / JavaFX) and infrastructure wiring with Spring. To proceed:

- Scaffold the packages and copy the examples in the appropriate modules.
- Implement repository mappers and small integration tests against H2 and Flyway.
- Add a `UiFlowManager` instance and wire FXML controllers through Spring to allow dependency injection.

If you want, I can also:
- Generate a starter skeleton (sources + FXML) following this doc.
- Add tests and a sample Flyway migration file.
