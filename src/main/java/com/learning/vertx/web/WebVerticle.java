package com.learning.vertx.web;

import com.learning.vertx.dto.ProjectDTO;
import com.learning.vertx.model.Project;
import com.learning.vertx.model.Task;
import com.learning.vertx.service.ProjectServiceImpl;
import io.vertx.core.*;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import lombok.RequiredArgsConstructor;
import org.hibernate.cfg.Configuration;
import org.hibernate.reactive.provider.ReactiveServiceRegistryBuilder;
import org.hibernate.reactive.stage.Stage;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;

@RequiredArgsConstructor
public class WebVerticle extends AbstractVerticle {
  private final ProjectServiceImpl projectService;

  @Override
  public void start(Promise<Void> startPromise) {
    Router router = Router.router(vertx);
    HttpServer server = vertx.createHttpServer();
//    router.route().handler(CorsHandler.create("*"));

    router.get("/api/hello").handler(ctx -> {
      ctx.response().end("Hello, World!");  // Ensure this line is reached
    });
    router.post("/api/v1/projects").handler(routingContext -> {
      ProjectDTO jsonObject = routingContext.body().asJsonObject().mapTo(ProjectDTO.class);
      projectService.creatProject(jsonObject)
        .onFailure(e -> routingContext.response().setStatusCode(400).setStatusMessage("failed"))
        .onSuccess(result -> routingContext.response().setStatusCode(200).setStatusMessage("success").end(voidAsyncResult -> voidAsyncResult.map(result)));
      routingContext.end();
    });

    router.put("/api/v1/projects/:id/update").handler(routingContext -> {
      ProjectDTO projectDTO = routingContext.body().asJsonObject().mapTo(ProjectDTO.class);
      String id = routingContext.pathParam("id");
      projectService.updateProject(projectDTO, id)
        .onFailure(e -> routingContext.response().setStatusCode(404).setStatusMessage("Not found"))
        .onSuccess(result -> routingContext.response().setStatusCode(200).setStatusMessage("success").end(voidAsyncResult -> voidAsyncResult.map(result)));
    });

    router.get("/api/v1/projects/:id").handler(routingContext -> {
      String id = routingContext.pathParam("id");
      projectService.getProjectById(id)
        .onFailure(e -> routingContext.response().setStatusCode(400))
        .onSuccess(result -> routingContext.response().setStatusCode(200).setStatusMessage("success").end((Handler<AsyncResult<Void>>) result));
    });

    router.get("/api/v1/projects").handler(routingContext -> projectService.getAllProjects()
      .onFailure(e -> routingContext.response().setStatusCode(500))
      .onSuccess(result -> routingContext.response().setStatusCode(200).setStatusMessage("success").end("helloe")));

    router.delete("/api/v1/projects/:id").handler(routingContext -> projectService.deleteProject(routingContext.pathParam("id"))
      .onFailure(e -> routingContext.response().setStatusCode(400).setStatusMessage("Error"))
      .onSuccess(result -> routingContext.response().setStatusCode(201).setStatusMessage("Successful").end()));

    JsonObject config = config();
    Integer port = config.getInteger("port");

    server.requestHandler(router).listen(8080);

  }

  public static void main(String[] args){
    Properties properties = new Properties();

    //db url
    properties.put("hibernate.connection.url", "jdbc:mysql://localhost:3306/hibernate_vertx_db?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
    properties.put("hibernate.connection.username", "root");
    properties.put("hibernate.connection.password", "Summer123!");
    properties.put("hibernate.show_sql", true);
    properties.put("hibernate.format_sql", true);

    // schema generation
    properties.put("jakarta.persistence.schema-generation.database.action", "create");


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

    ProjectServiceImpl  projectService = new ProjectServiceImpl(sessionFactory);

    WebVerticle webVerticle = new WebVerticle(projectService);

    DeploymentOptions options = new DeploymentOptions();
    JsonObject config = new JsonObject();
    config.put("port", 8000);
    options.setConfig(config);

    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(webVerticle);
  }

}
