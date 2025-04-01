package com.learning.vertx.repository;

import com.learning.vertx.dto.TaskDTO;
import io.vertx.core.Future;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {
  Future<TaskDTO> creatTask(TaskDTO taskDTO);

  Future<TaskDTO> updateTask(TaskDTO taskDTO, Integer id);

  Future<Optional<TaskDTO>> getTaskById(Integer id);

  Future<Void> deleteTask(Integer id);

  Future<List<TaskDTO>> getUserTasks(String userId);
}
