package com.learning.vertx.dto.mapper;

import com.learning.vertx.dto.TaskDTO;
import com.learning.vertx.model.Task;

import java.util.function.Function;

public class TaskMapper implements Function<TaskDTO, Task> {
  @Override
  public Task apply(TaskDTO taskDTO) {
    Task task = new Task();
    task.setUserId(taskDTO.getUserId());
    task.setContent(taskDTO.getContent());
    task.setCompleted(taskDTO.getCompleted());
    task.setCreatedAt(taskDTO.getCreatedAt());

    return task;
  }
}
