package com.learning.vertx.repository;

import com.learning.vertx.dto.ProjectDTO;
import com.learning.vertx.model.Project;
import io.vertx.core.Future;

import java.util.List;

public interface ProjectRepository {
  Future<Project> creatProject(ProjectDTO taskDTO);

  Future<ProjectDTO> updateProject(ProjectDTO taskDTO, String id);

  Future<ProjectDTO> getProjectById(String id);

  Future<Void> deleteProject(String id);

  Future<List<ProjectDTO>> getAllProjects();
}
