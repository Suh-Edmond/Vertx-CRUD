package com.learning.vertx.dto.mapper;

import com.learning.vertx.dto.TaskDTO;
import com.learning.vertx.model.Task;

import java.util.function.Function;

public class TaskDTOMapper implements Function<Task, TaskDTO> {

  @Override
  public TaskDTO apply(Task task) {

    TaskDTO taskDTO = new TaskDTO();
    taskDTO.setCompleted(task.getCompleted());
    taskDTO.setContent(task.getContent());
    taskDTO.setCreatedAt(task.getCreatedAt());
    taskDTO.setUserId(task.getUserId());
    taskDTO.setId(task.getId());

    return taskDTO;
  }
}
