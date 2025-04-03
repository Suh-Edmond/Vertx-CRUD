package com.learning.vertx.service;

import com.learning.vertx.dto.ProjectDTO;
import com.learning.vertx.dto.TaskDTO;
import com.learning.vertx.model.Project;
import com.learning.vertx.model.Task;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.hibernate.cfg.Configuration;
import org.hibernate.reactive.provider.ReactiveServiceRegistryBuilder;
import org.hibernate.reactive.stage.Stage;
import org.hibernate.service.ServiceRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.Properties;
import java.util.UUID;

@ExtendWith(VertxExtension.class)
class ProjectServiceImplTest {

  private ProjectServiceImpl projectService;
  private TaskServiceImpl taskService;
  private ProjectDTO projectDTO;
  private TaskDTO taskDTO;

  @BeforeEach
  void setUp(Vertx vertx, VertxTestContext vertxTestContext) {
    // 1. Create properties with config data
    Properties properties = new Properties();

    //db url
    properties.put("hibernate.connection.url", "jdbc:mysql://localhost:3306/hibernate_vertx_db?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
    properties.put("hibernate.connection.username", "root");
    properties.put("hibernate.connection.password", "Summer123!");
    properties.put("hibernate.show_sql", true);
    properties.put("hibernate.format_sql", true);

    // schema generation
    properties.put("jakarta.persistence.schema-generation.database.action", "update");


    // 2. Create Hibernate Configration
    Configuration configuration = new Configuration();
    configuration.setProperties(properties);
    configuration.addAnnotatedClass(Task.class);
    configuration.addAnnotatedClass(Project.class);

    // 3. Create Service Registry
    ServiceRegistry serviceRegistry = new ReactiveServiceRegistryBuilder()
      .applySettings(configuration.getProperties()).build();

    // 4. Create SessionFactory
    Stage.SessionFactory sessionFactory = configuration
      .buildSessionFactory(serviceRegistry)
      .unwrap(Stage.SessionFactory.class);

    projectService = new ProjectServiceImpl(sessionFactory);
    taskService = new TaskServiceImpl(sessionFactory);

    projectDTO = new ProjectDTO();
    projectDTO.setDescription("some description");
    projectDTO.setName("new project");

    taskDTO = new TaskDTO();
    taskDTO.setUserId(UUID.randomUUID().toString());
    taskDTO.setCreatedAt(LocalDateTime.now());
    taskDTO.setContent("created content");
    taskDTO.setCompleted(Boolean.TRUE);

    vertxTestContext.completeNow();
  }

  @Test
  void creatProject(Vertx vertx, VertxTestContext vertxTestContext) {

    vertxTestContext.verify(() -> projectService.creatProject(projectDTO)
      .compose(project -> taskService.creatTask(taskDTO, project))
      .onFailure(vertxTestContext::failNow)
      .onSuccess(task -> {

        Assertions.assertEquals(task.getCreatedAt(), taskDTO.getCreatedAt());
        Assertions.assertEquals(task.getUserId(), taskDTO.getUserId());
        Assertions.assertEquals(task.getCompleted(), taskDTO.getCompleted());
        Assertions.assertEquals(task.getId(), taskDTO.getId());
      })).completeNow();
  }

  @Test
  void updateProject(Vertx vertx, VertxTestContext vertxTestContext) {
    taskDTO.setCompleted(Boolean.FALSE);
    taskDTO.setContent("Updated task content");

    vertxTestContext.verify(() -> {
      projectService.creatProject(projectDTO)
        .compose(project -> {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setId(project.getId());
        projectDTO.setDescription("Updated Project description");
        projectDTO.setName("Updated name");

        taskService.creatTask(taskDTO, project);

        return projectService.updateProject(projectDTO, project.getId());
      }).onFailure(vertxTestContext::failNow)
        .onSuccess(result -> {
          Assertions.assertEquals("Updated name", result.getName());
          Assertions.assertEquals("Updated Project description", result.getDescription());
        });
    }).completeNow();

  }

  @Test
  void getProjectById(Vertx vertx, VertxTestContext vertxTestContext) {
    taskDTO.setCompleted(Boolean.FALSE);
    taskDTO.setContent("get task content");

    vertxTestContext.verify(() -> {
      projectService.creatProject(projectDTO)
        .compose(created -> {

        Assertions.assertEquals(created.getName(), projectDTO.getName());
        Assertions.assertEquals(created.getDescription(), projectDTO.getDescription());

        taskService.creatTask(taskDTO, created);
        return projectService.getProjectById(created.getId());
      }).onFailure(vertxTestContext::failNow)
        .onSuccess(result -> {
          Assertions.assertEquals(projectDTO.getName(), result.getName());
          Assertions.assertEquals(projectDTO.getDescription(), result.getDescription());
          Assertions.assertEquals(projectDTO.getId(), result.getId());
        });
    }).completeNow();
  }

  @Test
  void deleteProject(Vertx vertx, VertxTestContext vertxTestContext) {
    taskDTO.setCompleted(Boolean.FALSE);
    taskDTO.setContent("Delete task content");

    vertxTestContext.verify(() -> {
      projectService.creatProject(projectDTO)
        .compose(created -> {
          taskService.creatTask(taskDTO, created);
          return projectService.deleteProject(created.getId());
        })
        .compose(r -> projectService.getProjectById(projectDTO.getId())).onFailure(vertxTestContext::failNow).onSuccess(Assertions::assertNull);
    }).completeNow();
  }
}
