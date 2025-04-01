package com.learning.vertx.service;

import com.learning.vertx.dto.TaskDTO;
import com.learning.vertx.dto.mapper.TaskDTOMapper;
import com.learning.vertx.dto.mapper.TaskMapper;
import com.learning.vertx.model.Task;
import com.learning.vertx.repository.TaskRepository;
import io.vertx.core.Future;
import jakarta.persistence.criteria.*;
import org.hibernate.reactive.stage.Stage;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class TaskServiceImpl implements TaskRepository {
  private final Stage.SessionFactory sessionFactory;

  public TaskServiceImpl(Stage.SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public Future<TaskDTO> creatTask(TaskDTO taskDTO) {
    TaskMapper taskMapper = new TaskMapper();
    Task task = taskMapper.apply(taskDTO);

    TaskDTOMapper taskDTOMapper = new TaskDTOMapper();

    CompletionStage<Void> result = sessionFactory.withTransaction((s, t) -> s.persist(task));

    return Future.fromCompletionStage(result).map(v -> taskDTOMapper.apply(task));
  }

  @Override
  public Future<TaskDTO> updateTask(TaskDTO taskDTO, Integer id) {
    CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();
    CriteriaUpdate<Task> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(Task.class);
    Root<Task> root = criteriaUpdate.from(Task.class);
    Predicate predicate = criteriaBuilder.equal(root.get("id"), id);

    criteriaUpdate.set("content", taskDTO.getContent());
    criteriaUpdate.set("completed", taskDTO.getCompleted());
    criteriaUpdate.where(predicate);

    CompletionStage<Integer> result = sessionFactory.withTransaction((s, t) -> s.createQuery(criteriaUpdate).executeUpdate());

    return Future.fromCompletionStage(result).map(r ->taskDTO);
  }

  @Override
  public Future<Optional<TaskDTO>> getTaskById(Integer id) {
    TaskDTOMapper taskDTOMapper = new TaskDTOMapper();
    CompletionStage<Task> taskCompletionStage = sessionFactory.withTransaction((s, t) -> s.find(Task.class, id));

    return Future.fromCompletionStage(taskCompletionStage)
      .map(Optional::ofNullable)
      .map(r -> r.map(taskDTOMapper));
  }

  @Override
  public Future<Void> deleteTask(Integer id) {
    CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();
    CriteriaDelete<Task> criteriaDelete = criteriaBuilder.createCriteriaDelete(Task.class);
    Root<Task> root = criteriaDelete.from(Task.class);
    Predicate predicate = criteriaBuilder.equal(root.get("id"), id);
    criteriaDelete.where(predicate);

    //The executeUpdate method will either return 0 or 1 if entity exist or not respectively
    CompletionStage<Integer> result = sessionFactory.withTransaction((s, t) -> s.createQuery(criteriaDelete).executeUpdate());

    return Future.fromCompletionStage(result).compose(r -> Future.succeededFuture());
  }

  @Override
  public Future<List<TaskDTO>> getUserTasks(String userId) {
    CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();
    CriteriaQuery<Task> criteriaQuery = criteriaBuilder.createQuery(Task.class);
    Root<Task> root = criteriaQuery.from(Task.class);
    Predicate predicate = criteriaBuilder.equal(root.get("userId"), userId);
    criteriaQuery.where(predicate);
    TaskDTOMapper dtoMapper = new TaskDTOMapper();

    CompletionStage<List<Task>> listCompletionStage = sessionFactory.withTransaction((s, t) -> s.createQuery(criteriaQuery).getResultList());

    return Future.fromCompletionStage(listCompletionStage).map(r -> r.stream().map(dtoMapper).collect(Collectors.toList()));
  }
}
