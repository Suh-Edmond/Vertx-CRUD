package com.learning.vertx.service;

import com.learning.vertx.dto.ProjectDTO;
import com.learning.vertx.dto.mapper.ProjectDTOMapper;
import com.learning.vertx.model.Project;
import com.learning.vertx.repository.ProjectRepository;
import io.vertx.core.Future;
import jakarta.persistence.criteria.*;
import org.hibernate.reactive.stage.Stage;

import java.util.concurrent.CompletionStage;

public class ProjectServiceImpl implements ProjectRepository {
  private final Stage.SessionFactory sessionFactory;

  public ProjectServiceImpl(Stage.SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public Future<Project> creatProject(ProjectDTO projectDTO) {
    Project project = new Project();
    project.setName(projectDTO.getName());
    project.setDescription(projectDTO.getDescription());

    CompletionStage<Void> completionStage = sessionFactory.withTransaction((session -> session.persist(project)));

    return Future.fromCompletionStage(completionStage).map(v -> project);
  }

  @Override
  public Future<ProjectDTO> updateProject(ProjectDTO projectDTO, String id) {
    CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();
    CriteriaUpdate<Project> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(Project.class);
    Root<Project> root = criteriaUpdate.from(Project.class);
    Predicate predicate = criteriaBuilder.equal(root.get("id"), id);

    CriteriaUpdate<Project> where = criteriaUpdate.where(predicate);

    where.set("name", projectDTO.getName());
    where.set("description", projectDTO.getDescription());

    CompletionStage<Integer> completionStage = sessionFactory.withTransaction((s, t) -> s.createQuery(criteriaUpdate).executeUpdate());
    return Future.fromCompletionStage(completionStage).map(r -> projectDTO);
  }

  @Override
  public Future<ProjectDTO> getProjectById(String id) {
    CompletionStage<Project> completionStage = sessionFactory.withTransaction((s, t) -> s.find(Project.class, id));

    return Future.fromCompletionStage(completionStage).map(r -> new ProjectDTOMapper().apply(r));
  }

  @Override
  public Future<Void> deleteProject(String id) {
    CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();
    CriteriaDelete<Project> criteriaDelete = criteriaBuilder.createCriteriaDelete(Project.class);
    Root<Project> root = criteriaDelete.from(Project.class);
    Predicate predicate = criteriaBuilder.equal(root.get("id"), id);

    criteriaDelete.where(predicate);

    CompletionStage<Integer> completionStage = sessionFactory.withTransaction((s, t) -> s.createQuery(criteriaDelete).executeUpdate());

    return Future.fromCompletionStage(completionStage).compose(r -> Future.succeededFuture());
  }
}
