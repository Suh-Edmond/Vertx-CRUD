package com.learning.vertx.repository;

import com.learning.vertx.dto.TaskDTO;
import com.learning.vertx.model.Project;
import io.vertx.core.Future;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {
  Future<TaskDTO> creatTask(TaskDTO taskDTO, Project project);

  Future<TaskDTO> updateTask(TaskDTO taskDTO, String id);

  Future<Optional<TaskDTO>> getTaskById(String id);

  Future<Void> deleteTask(String id);

  Future<List<TaskDTO>> getUserTasks(String userId);
}
