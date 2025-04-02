package com.learning.vertx.dto.mapper;

import com.learning.vertx.dto.ProjectDTO;
import com.learning.vertx.model.Project;

import java.util.function.Function;

public class ProjectDTOMapper implements Function<Project, ProjectDTO> {
  @Override
  public ProjectDTO apply(Project project) {
    ProjectDTO projectDTO = new ProjectDTO();
    projectDTO.setName(project.getName());
    projectDTO.setDescription(project.getDescription());
    projectDTO.setId(project.getId());

    return projectDTO;
  }
}
