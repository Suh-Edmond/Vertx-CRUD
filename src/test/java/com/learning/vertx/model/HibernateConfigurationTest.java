//package com.learning.vertx.model;
//
//import com.learning.vertx.dto.TaskDTO;
//import com.learning.vertx.service.ProjectServiceImpl;
//import com.learning.vertx.service.TaskServiceImpl;
//import io.vertx.core.Vertx;
//import io.vertx.junit5.VertxExtension;
//import io.vertx.junit5.VertxTestContext;
//import org.hibernate.cfg.Configuration;
//import org.hibernate.reactive.provider.ReactiveServiceRegistryBuilder;
//import org.hibernate.reactive.stage.Stage;
//import org.hibernate.service.ServiceRegistry;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//
//import java.time.LocalDateTime;
//import java.util.Properties;
//import java.util.UUID;
//
//@ExtendWith(VertxExtension.class)
//public class HibernateConfigurationTest {
//
//  private TaskServiceImpl taskService;
//  private ProjectServiceImpl projectService;
//  private TaskDTO taskDTO;
//
//  @BeforeEach
//  void setUp(Vertx vertx, VertxTestContext vertxTestContext) {
//    // 1. Create properties with config data
//    Properties properties = new Properties();
//
//    //db url
//    properties.put("hibernate.connection.url", "jdbc:mysql://localhost:3306/hibernate_vertx_db?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
//    properties.put("hibernate.connection.username", "root");
//    properties.put("hibernate.connection.password", "Summer123!");
//    properties.put("hibernate.show_sql", true);
//    properties.put("hibernate.format_sql", true);
//
//    // schema generation
//    properties.put("jakarta.persistence.schema-generation.database.action", "create");
//
//
//    // 2. Create Hibernate Configration
//    Configuration configuration = new Configuration();
//    configuration.setProperties(properties);
//    configuration.addAnnotatedClass(Project.class);
//    configuration.addAnnotatedClass(Task.class);
//
//    // 3. Create Service Registry
//    ServiceRegistry serviceRegistry = new ReactiveServiceRegistryBuilder()
//      .applySettings(configuration.getProperties()).build();
//
//    // 4. Create SessionFactory
//    Stage.SessionFactory sessionFactory = configuration
//      .buildSessionFactory(serviceRegistry)
//      .unwrap(Stage.SessionFactory.class);
//
//    projectService = new ProjectServiceImpl(sessionFactory);
//    taskService = new TaskServiceImpl(sessionFactory);
//
//    taskDTO = new TaskDTO();
//    taskDTO.setId(null);
//    taskDTO.setCreatedAt(LocalDateTime.now());
//    taskDTO.setContent("some content");
//    taskDTO.setCompleted(Boolean.TRUE);
//    taskDTO.setUserId(UUID.randomUUID().toString());
//
//    vertxTestContext.completeNow();
//  }
//
//  @Test
//  void createTaskTest(Vertx vertx, VertxTestContext vertxTestContext) {
//    vertxTestContext.verify(() -> {
////      projectService.creatProject()
//    });
//    vertxTestContext.completeNow();
//  }
//
//  @Test
//  void getTaskByIdDoesNotExistTest(Vertx vertx, VertxTestContext vertxTestContext){
//    vertxTestContext.verify(() -> taskService.getTaskById(UUID.randomUUID().toString())
//      .onSuccess(r -> Assertions.assertTrue(r.isEmpty()))
//      .onFailure(vertxTestContext::failNow));
//
//    vertxTestContext.completeNow();
//  }
//
//  @Test
//  void getTaskByIdExistTest(Vertx vertx, VertxTestContext vertxTestContext){
//    vertxTestContext.verify(() -> taskService.creatTask(taskDTO)
//      .compose(r -> taskService.getTaskById(r.getId()))
//      .onFailure(vertxTestContext::failNow)
//      .onSuccess(result -> Assertions.assertTrue(result.isPresent())));
//
//    vertxTestContext.completeNow();
//  }
//
//  @Test
//  void removeTaskTest(Vertx vertx, VertxTestContext vertxTestContext) {
//    vertxTestContext.verify(() -> {
//      taskService.creatTask(taskDTO)
//        .compose(result ->  {
//          Assertions.assertEquals(UUID.randomUUID().toString(), result.getId());
//          return taskService.deleteTask(result.getId());
//        }).compose(r -> taskService.getTaskById(UUID.randomUUID().toString()))
//        .onFailure(vertxTestContext::failNow)
//        .onSuccess(r -> {
//          Assertions.assertTrue(r.isEmpty());
//        });
//    }).completeNow();
//  }
//
//  @Test
//  void updateTaskTest(Vertx vertx, VertxTestContext vertxTestContext){
//    String id = UUID.randomUUID().toString();
//    vertxTestContext.verify(() -> {
//      taskService.creatTask(taskDTO)
//        .compose(res -> {
//          Assertions.assertNotNull(res);
//          Assertions.assertEquals(id, res.getId());
//
//          TaskDTO updatedTask = new TaskDTO(res.getId(), res.getUserId(), Boolean.FALSE, "Updated Content", res.getCreatedAt());
//
//          return taskService.updateTask(updatedTask, id);
//        }).compose(r -> {
//          Assertions.assertEquals(r.getContent(), "Updated Content");
//          Assertions.assertFalse(r.getCompleted());
//
//          return taskService.getTaskById(r.getId());
//      }).onFailure(vertxTestContext::failNow)
//        .onSuccess(r -> {
//          Assertions.assertTrue(r.isPresent());
//          Assertions.assertEquals("Updated Content", r.get().getContent());
//          Assertions.assertFalse( r.get().getCompleted());
//        });
//    }).completeNow();
//  }
//
//  @Test
//  void getUserTasksTest(Vertx vertx, VertxTestContext vertxTestContext){
//    vertxTestContext.verify(() -> {
//      taskService.creatTask(taskDTO)
//        .compose(res -> {
//          return taskService.getUserTasks(res.getUserId());
//        })
//        .onFailure(vertxTestContext::failNow)
//        .onSuccess(res -> {
//          Assertions.assertEquals(1, res.size());
//          Assertions.assertEquals(taskDTO.getContent(), res.get(0).getContent());
//          Assertions.assertEquals(taskDTO.getCompleted(), res.get(0).getCompleted());
//          Assertions.assertEquals(taskDTO.getCreatedAt(), res.get(0).getCreatedAt());
//          Assertions.assertEquals(taskDTO.getUserId(), res.get(0).getUserId());
//        });
//
//    }).completeNow();
//  }
//}
