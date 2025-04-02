package com.learning.vertx.model;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "tasks")
@Data
public class Task {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID, generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  private String id;

  private String content;

  private Boolean completed;

  private String userId;

  private LocalDateTime createdAt;

  @ManyToOne()
  private Project project;

  public Task() {
  }

  @Override
  public int hashCode() {
    return Objects.hash(content, completed, userId, createdAt);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Task task = (Task) o;
    return Objects.equals(id, task.id);
  }


}
