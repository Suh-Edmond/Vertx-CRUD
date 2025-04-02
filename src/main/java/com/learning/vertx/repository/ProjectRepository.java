package com.learning.vertx.repository;

import com.learning.vertx.dto.ProjectDTO;
import com.learning.vertx.model.Project;
import io.vertx.core.Future;

public interface ProjectRepository {
  Future<Project> creatProject(ProjectDTO taskDTO);

  Future<ProjectDTO> updateProject(ProjectDTO taskDTO, String id);

  Future<ProjectDTO> getProjectById(String id);

  Future<Void> deleteProject(String id);
}
