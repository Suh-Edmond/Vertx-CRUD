package com.learning.vertx.web;

import com.learning.vertx.dto.ProjectDTO;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.MySQLContainer;

@ExtendWith(VertxExtension.class)
@ExtendWith(MockitoExtension.class)
@Slf4j
class WebVerticleTest {

  private static MySQLContainer mySQLContainer;

  @InjectMocks
  private WebVerticle verticle;

  private WebClient webClient;
  private Vertx vertx;

  @BeforeEach
  void setUp(VertxTestContext vertxTestContext) {
    vertx = Vertx.vertx();
    mySQLContainer = new MySQLContainer("mysql:latest")
      .withDatabaseName("testdb")
      .withUsername("user")
      .withPassword("password");
    mySQLContainer.start();

    DeploymentOptions options = new DeploymentOptions();
    JsonObject config = new JsonObject();
    config.put("port", 8000);
    options.setConfig(config);

    webClient = WebClient.create(vertx);

    vertx.deployVerticle(verticle, options).onSuccess(result -> vertxTestContext.completeNow()).onFailure(vertxTestContext::failNow);
  }

  @AfterEach
  void tearDown(VertxTestContext testContext) {
    vertx.close();
    testContext.completeNow();
  }

  @AfterAll
  static void tearDownContainer() {
    mySQLContainer.stop();
  }

  @Test
  void findProjectByIdExistTest(VertxTestContext vertxTestContext) {
    ProjectDTO projectDTO = new ProjectDTO();
    projectDTO.setName("First Project");
    projectDTO.setDescription("some description");

    vertxTestContext.verify(() -> webClient.postAbs("http://localhost:8000/api/v1/projects/create")
      .putHeader("accept", "application/json")
      .putHeader("content-type", "application/json")
      .sendJson(projectDTO).compose(bufferHttpResponse -> {
        return webClient.getAbs("http://localhost:8000/api/v1/projects/"+ bufferHttpResponse.bodyAsJson(ProjectDTO.class).getId())
          .putHeader("accept", "application/json")
          .putHeader("content-type", "application/json")
          .send()
          .onFailure(vertxTestContext::failNow)
          .onSuccess(result -> {
            Assertions.assertEquals(200, result.statusCode());
            Assertions.assertEquals("success", result.statusMessage());
            Assertions.assertEquals(projectDTO, result.bodyAsJson(ProjectDTO.class));
            Assertions.assertEquals(projectDTO.getId(), result.bodyAsJsonObject().getString("id"));
            Assertions.assertEquals(projectDTO.getName(), result.bodyAsJsonObject().getString("name"));
            Assertions.assertEquals(projectDTO.getDescription(), result.bodyAsJsonObject().getString("description"));
          });
    })).completeNow();

  }

  @Test
  void findAllProjects(VertxTestContext vertxTestContext) {
    ProjectDTO projectDTO = new ProjectDTO();
    projectDTO.setName("First Project");
    projectDTO.setDescription("some description");

    webClient.getAbs("http://localhost:8000/api/v1/projects")
      .putHeader("accept", "application/json")
      .putHeader("content-type", "application/json")
      .sendJson(projectDTO)
      .compose(bufferHttpResponse -> {
        log.info(String.valueOf(bufferHttpResponse));
        return webClient.getAbs("http://localhost:8000/api/v1/projects")
          .putHeader("accept", "application/json")
          .putHeader("content-type", "application/json")
          .send()
          .onFailure(vertxTestContext::failNow)
          .onSuccess(result -> {
            log.info(String.valueOf(result));
            Assertions.assertEquals(200, result.statusCode());
            Assertions.assertEquals("error", result.statusMessage());
            Assertions.assertEquals(1, result.bodyAsJsonObject().size());
            Assertions.assertEquals(projectDTO.getId(), result.bodyAsJsonObject().getString("id"));
            Assertions.assertEquals(projectDTO.getName(), result.bodyAsJsonObject().getString("name"));
            Assertions.assertEquals(projectDTO.getDescription(), result.bodyAsJsonObject().getString("description"));
          });
    });
    vertxTestContext.completeNow();
  }



}
