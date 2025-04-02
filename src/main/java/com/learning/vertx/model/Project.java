package com.learning.vertx.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
@Data
public class Project {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID, generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  private String id;

  private String name;

  private String description;

  @OneToMany(mappedBy = "project")
  private List<Task> tasks = new ArrayList<>();
}
